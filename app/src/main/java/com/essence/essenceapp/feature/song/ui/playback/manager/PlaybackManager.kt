package com.essence.essenceapp.feature.song.ui.playback.manager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.RefreshStreamingUrlUseCase
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.PlaybackRepeatMode
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioOutputDetector
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioPlayerEngine
import com.essence.essenceapp.feature.song.ui.playback.engine.MediaPrefetcher
import com.essence.essenceapp.feature.song.ui.playback.mapper.toNowPlayingInfo
import com.essence.essenceapp.feature.song.ui.playback.model.NowPlayingInfo
import com.essence.essenceapp.feature.song.ui.playback.service.MediaPlaybackService
import com.essence.essenceapp.shared.playback.model.PlaybackQueue
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val PLAYBACK_TAG = "PLAYBACK_DEBUG"
private const val RESTART_THRESHOLD_MS = 3_000L
private const val WAKELOCK_TIMEOUT_MS = 30_000L
private const val NAVIGATION_DEBOUNCE_MS = 200L
private const val ERROR_RECOVERY_INITIAL_DELAY_MS = 4_000L
private const val ERROR_RECOVERY_BETWEEN_ATTEMPTS_MS = 5_000L
private const val MAX_ERROR_RECOVERY_ATTEMPTS = 3
private const val ERROR_RECOVERY_RESET_PLAYBACK_MS = 8_000L

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioPlayerEngine: AudioPlayerEngine,
    private val audioOutputDetector: AudioOutputDetector,
    private val getSongUseCase: GetSongUseCase,
    private val refreshStreamingUrlUseCase: RefreshStreamingUrlUseCase,
    private val queueController: PlaybackQueueController,
    private val historyRecorder: PlaybackHistoryRecorder,
    private val likeController: PlaybackLikeController,
    private val mediaPrefetcher: MediaPrefetcher
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val powerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private val transitionWakeLock by lazy {
        powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "EssenceApp:PlaybackTransitionLock"
        ).apply { setReferenceCounted(false) }
    }

    private val _nowPlaying = MutableStateFlow<NowPlayingInfo?>(null)
    val nowPlaying: StateFlow<NowPlayingInfo?> = _nowPlaying.asStateFlow()

    val queue: StateFlow<PlaybackQueue?> = queueController.queue

    val isCurrentSongLiked: StateFlow<Boolean> = likeController.isLiked

    private val resolvedSongs = mutableMapOf<String, Song>()

    fun getResolvedSong(lookup: String): Song? = resolvedSongs[lookup]

    fun currentVideoId(): String? = _nowPlaying.value?.songLookup

    private val _isResolvingNextSong = MutableStateFlow(false)

    private val _manualErrorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PlaybackUiState> = combine(
        audioPlayerEngine.state,
        queueController.queue,
        audioOutputDetector.outputType,
        _isResolvingNextSong,
        _manualErrorMessage
    ) { audioState, queue, output, resolving, manualError ->
        PlaybackUiState(
            isPlaying = !resolving && audioState.isPlaying,
            isBuffering = audioState.isBuffering || resolving,
            positionMs = audioState.positionMs,
            durationMs = audioState.durationMs,
            repeatMode = audioState.repeatMode,
            canGoPrevious = queue?.canGoPrevious == true || audioState.durationMs > 0L,
            canGoNext = queue?.canGoNext == true,
            errorMessage = manualError ?: audioState.errorMessage,
            audioOutput = output
        )
    }.stateIn(scope, SharingStarted.Eagerly, PlaybackUiState())

    private var hasEndedHandled = false
    private var lastPositionMs: Long = 0L
    private var errorRecoveryJob: Job? = null
    private var errorRecoveryAttempts: Int = 0
    private var lastStablePlaybackStartMs: Long = 0L
    private var resolveSongJob: Job? = null
    private var sourceRefreshJob: Job? = null
    private var prefetchJob: Job? = null
    private var pendingNavigationJob: Job? = null
    private var lastNavigationTime: Long = 0L

    private fun recordSkippedForCurrentIfNeeded(nextLookup: String) {
        val current = _nowPlaying.value ?: return
        if (current.songLookup == nextLookup) return
        if (historyRecorder.isAlreadyRecorded()) return
        if (!historyRecorder.shouldRecordOnSwitch(lastPositionMs)) return
        historyRecorder.recordSkipped(current.songId, lastPositionMs)
    }

    init {
        scope.launch {
            audioPlayerEngine.state.collect { audioState ->
                lastPositionMs = audioState.positionMs

                if (
                    !historyRecorder.isAlreadyRecorded() &&
                    historyRecorder.hasReachedListenThreshold(lastPositionMs)
                ) {
                    val info = _nowPlaying.value
                    if (info != null) {
                        historyRecorder.recordListened(info.songId, lastPositionMs)
                    }
                }

                if (audioState.hasEnded && !historyRecorder.isAlreadyRecorded()) {
                    val info = _nowPlaying.value
                    if (info != null) {
                        historyRecorder.recordCompleted(info.songId, lastPositionMs)
                    }
                }

                if (
                    audioState.hasEnded &&
                    audioState.repeatMode == PlaybackRepeatMode.Off &&
                    !hasEndedHandled
                ) {
                    hasEndedHandled = true
                    acquireTransitionWakeLock()
                    goNext()
                }

                if (audioState.requiresSourceRefresh && sourceRefreshJob?.isActive != true) {
                    val info = _nowPlaying.value
                    if (info != null && info.songLookup.isNotBlank()) {
                        audioPlayerEngine.clearSourceRefreshRequest()
                        sourceRefreshJob = scope.launch { handleSourceRefresh(info.songLookup) }
                    } else {
                        audioPlayerEngine.clearSourceRefreshRequest()
                    }
                }
                if (
                    audioState.errorMessage != null &&
                    !audioState.requiresSourceRefresh &&
                    audioState.repeatMode == PlaybackRepeatMode.Off &&
                    queueController.canGoNext
                ) {
                    if (errorRecoveryJob?.isActive != true) {
                        errorRecoveryJob = scope.launch { handleErrorRecoveryStep() }
                    }
                } else {
                    errorRecoveryJob?.cancel()
                    errorRecoveryJob = null
                    if (audioState.isPlaying) {
                        if (lastStablePlaybackStartMs == 0L) {
                            lastStablePlaybackStartMs = System.currentTimeMillis()
                        } else if (
                            errorRecoveryAttempts > 0 &&
                            System.currentTimeMillis() - lastStablePlaybackStartMs >= ERROR_RECOVERY_RESET_PLAYBACK_MS
                        ) {
                            Log.d(PLAYBACK_TAG, "Reproduccion estable, reseteando contador de recuperacion")
                            errorRecoveryAttempts = 0
                        }
                    } else {
                        lastStablePlaybackStartMs = 0L
                    }
                }
            }
        }
    }

    // Queue

    fun setQueue(queue: PlaybackQueue) = queueController.setQueue(queue)

    fun setQueueFromItems(
        items: List<PlaybackQueueItem>,
        startIndex: Int,
        sourceKey: String
    ) = queueController.setQueueFromItems(items, startIndex, sourceKey)

    fun clearQueue() = queueController.clear()

    //Playback principal

    fun playSong(song: Song, forceRestart: Boolean = false) {
        val info = song.toNowPlayingInfo()
        if (info == null) {
            Log.w(PLAYBACK_TAG, "streamingUrl ausente para ${song.hlsMasterKey} (transitorio, refresh en curso)")
            return
        }

        val current = _nowPlaying.value
        if (
            !forceRestart &&
            current?.songLookup == info.songLookup &&
            audioPlayerEngine.isPlayingUrl(info.streamingUrl)
        ) {
            Log.d(PLAYBACK_TAG, "La misma cancion ya esta cargada, se conserva la reproduccion actual")
            _nowPlaying.value = info
            queueController.alignIndex(info.songLookup)
            return
        }

        recordSkippedForCurrentIfNeeded(info.songLookup)

        resolvedSongs[song.hlsMasterKey] = song
        queueController.alignIndex(info.songLookup)

        _manualErrorMessage.value = null
        _nowPlaying.value = info
        historyRecorder.resetForNewSong()
        hasEndedHandled = false
        lastPositionMs = 0L
        errorRecoveryAttempts = 0
        lastStablePlaybackStartMs = 0L
        errorRecoveryJob?.cancel()
        errorRecoveryJob = null
        likeController.setLiked(song.isLiked)
        audioPlayerEngine.play(
            url = info.streamingUrl,
            forceRestart = forceRestart,
            title = info.title,
            artist = info.artistName,
            artworkUri = resolveImageUrl(info.imageKey),
            mediaId = info.songLookup
        )
        startMediaService()
        mediaPrefetcher.prefetch(info.streamingUrl)
        prefetchNextSong()
    }

    fun setNowPlaying(info: NowPlayingInfo) {
        _nowPlaying.value = info
    }

    fun clearNowPlaying() {
        val info = _nowPlaying.value
        if (
            info != null &&
            !historyRecorder.isAlreadyRecorded() &&
            historyRecorder.shouldRecordOnSwitch(lastPositionMs)
        ) {
            historyRecorder.recordSkipped(info.songId, lastPositionMs)
        }
        mediaPrefetcher.cancel()
        audioPlayerEngine.stop()
        errorRecoveryJob?.cancel()
        errorRecoveryJob = null
        errorRecoveryAttempts = 0
        lastStablePlaybackStartMs = 0L
        _manualErrorMessage.value = null
        _nowPlaying.value = null
        stopMediaService()
    }

    fun onAction(action: PlaybackAction) {
        when (action) {
            PlaybackAction.Play -> {
                if (uiState.value.errorMessage != null) {
                    replayCurrentSong()
                } else {
                    audioPlayerEngine.resume()
                }
            }
            PlaybackAction.Pause -> audioPlayerEngine.pause()
            PlaybackAction.Stop -> clearNowPlaying()
            PlaybackAction.Next -> goNext()
            PlaybackAction.Previous -> goPrevious()
            is PlaybackAction.SeekTo -> audioPlayerEngine.seekTo(action.positionMs)
            is PlaybackAction.SeekBy -> {
                val next = (uiState.value.positionMs + action.deltaMs).coerceAtLeast(0L)
                audioPlayerEngine.seekTo(next)
            }
            PlaybackAction.ToggleRepeat -> audioPlayerEngine.toggleRepeatMode()
            PlaybackAction.ToggleLike -> {
                val info = _nowPlaying.value ?: return
                likeController.toggleLike(info.songId)
            }
        }
    }

    fun updateLikedState(isLiked: Boolean) {
        likeController.setLiked(isLiked)
    }

    fun release() {
        mediaPrefetcher.cancel()
        audioPlayerEngine.release()
        _nowPlaying.value = null
        queueController.clear()
        resolvedSongs.clear()
    }

    //Navegacion en la cola

    fun goNext() {
        val nextItem = queueController.advanceToNext()
        if (nextItem == null) {
            Log.d(PLAYBACK_TAG, "goNext: fin de cola")
            releaseTransitionWakeLock()
            return
        }
        handleNavigation(nextItem)
    }

    private fun goPrevious() {
        if (queueController.queue.value == null) {
            audioPlayerEngine.seekTo(0L)
            return
        }

        if (lastPositionMs > RESTART_THRESHOLD_MS) {
            Log.d(PLAYBACK_TAG, "goPrevious: reiniciando cancion actual (pos=${lastPositionMs}ms)")
            audioPlayerEngine.seekTo(0L)
            return
        }

        val prevItem = queueController.retreatToPrevious()
        if (prevItem == null) {
            Log.d(PLAYBACK_TAG, "goPrevious: inicio de cola")
            audioPlayerEngine.seekTo(0L)
            return
        }
        handleNavigation(prevItem)
    }

    private fun handleNavigation(item: PlaybackQueueItem) {
        val now = System.currentTimeMillis()
        val timeSinceLast = now - lastNavigationTime
        lastNavigationTime = now

        recordSkippedForCurrentIfNeeded(item.songLookup)

        _manualErrorMessage.value = null
        _isResolvingNextSong.value = true
        val cached = resolvedSongs[item.songLookup]
        _nowPlaying.value = NowPlayingInfo(
            songId = cached?.id ?: 0L,
            songLookup = item.songLookup,
            title = item.title,
            artistName = item.artistName,
            imageKey = item.imageKey,
            durationMs = item.durationMs,
            streamingUrl = ""
        )

        val isBurst = timeSinceLast <= NAVIGATION_DEBOUNCE_MS || pendingNavigationJob?.isActive == true
        pendingNavigationJob?.cancel()

        if (!isBurst) {
            playQueueItem(item)
            return
        }

        pendingNavigationJob = scope.launch {
            delay(NAVIGATION_DEBOUNCE_MS)
            playQueueItem(item)
        }
    }

    fun playQueueIndex(index: Int) {
        val item = queueController.moveToIndex(index) ?: return
        playQueueItem(item)
    }

    private fun playQueueItem(item: PlaybackQueueItem) {
        Log.d(PLAYBACK_TAG, "playQueueItem: lookup=${item.songLookup}")

        recordSkippedForCurrentIfNeeded(item.songLookup)

        hasEndedHandled = false
        acquireTransitionWakeLock()
        _manualErrorMessage.value = null

        val cached = resolvedSongs[item.songLookup]
        if (cached != null && !isStreamingUrlExpired(cached)) {
            _isResolvingNextSong.value = false
            startMediaService()
            playSong(cached, forceRestart = true)
            releaseTransitionWakeLock()
            return
        }

        _isResolvingNextSong.value = true

        _nowPlaying.value = NowPlayingInfo(
            songId = cached?.id ?: 0L,
            songLookup = item.songLookup,
            title = item.title,
            artistName = item.artistName,
            imageKey = item.imageKey,
            durationMs = item.durationMs,
            streamingUrl = ""
        )

        resolveSongJob?.cancel()

        resolveSongJob = scope.launch {
            try {
                val result = if (cached != null) {
                    refreshIfExpired(cached)
                } else {
                    getSongUseCase(item.songLookup)
                }
                result.onSuccess { song ->
                    _isResolvingNextSong.value = false
                    resolvedSongs[item.songLookup] = song
                    startMediaService()
                    playSong(song, forceRestart = true)
                }
                result.onFailure { error ->
                    Log.e(PLAYBACK_TAG, "Error resolviendo cancion del queue: ${error.message}")
                    _isResolvingNextSong.value = false
                    _manualErrorMessage.value = "No se pudo cargar la siguiente cancion."
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e(PLAYBACK_TAG, "Exception resolviendo cancion: ${e.message}", e)
                _isResolvingNextSong.value = false
            } finally {
                releaseTransitionWakeLock()
            }
        }
    }

    private fun isStreamingUrlExpired(song: Song): Boolean {
        if (song.streamingUrl.isNullOrBlank()) return true
        val expiresAt = song.streamingUrlExpiresAt ?: return false
        return expiresAt.isBefore(Instant.now())
    }

    private suspend fun refreshIfExpired(song: Song): Result<Song> {
        if (!isStreamingUrlExpired(song)) return Result.success(song)
        val videoId = song.hlsMasterKey
        return refreshStreamingUrlUseCase(
            currentSong = song,
            isStillCurrent = { currentVideoId() == videoId }
        )
    }

    private fun prefetchNextSong() {
        prefetchJob?.cancel()
        val nextItem = queueController.peekNext() ?: return
        val cached = resolvedSongs[nextItem.songLookup]
        if (cached != null && !isStreamingUrlExpired(cached)) return

        prefetchJob = scope.launch {
            try {
                val result = if (cached != null) {
                    refreshIfExpired(cached)
                } else {
                    getSongUseCase(nextItem.songLookup)
                }
                result.onSuccess { song ->
                    resolvedSongs[nextItem.songLookup] = song
                    Log.d(PLAYBACK_TAG, "Prefetch OK: ${nextItem.songLookup}")
                }
                result.onFailure { error ->
                    Log.w(PLAYBACK_TAG, "Prefetch fallo (no critico) ${nextItem.songLookup}: ${error.message}")
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.w(PLAYBACK_TAG, "Prefetch exception (no critico) ${nextItem.songLookup}: ${e.message}")
            }
        }
    }

    private suspend fun handleSourceRefresh(lookup: String) {
        Log.d(PLAYBACK_TAG, "URL expirada detectada por player, refrescando: $lookup")
        acquireTransitionWakeLock()
        _manualErrorMessage.value = null
        _isResolvingNextSong.value = true
        try {
            val currentSong = resolvedSongs[lookup]
            val result = if (currentSong != null) {
                refreshStreamingUrlUseCase(
                    currentSong = currentSong,
                    isStillCurrent = { currentVideoId() == lookup }
                )
            } else {
                getSongUseCase(lookup)
            }
            result.onSuccess { song ->
                _isResolvingNextSong.value = false
                resolvedSongs[lookup] = song
                playSong(song, forceRestart = true)
            }
            result.onFailure { error ->
                Log.e(PLAYBACK_TAG, "Error refrescando URL: ${error.message}")
                _isResolvingNextSong.value = false
                _manualErrorMessage.value = "No se pudo refrescar el stream."
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(PLAYBACK_TAG, "Exception refrescando URL: ${e.message}", e)
            _isResolvingNextSong.value = false
            _manualErrorMessage.value = "No se pudo refrescar el stream."
        } finally {
            releaseTransitionWakeLock()
        }
    }

    private fun replayCurrentSong() {
        val info = _nowPlaying.value ?: return
        val song = resolvedSongs[info.songLookup]
        if (song != null) {
            playSong(song, forceRestart = true)
        } else {
            val index = queueController.currentIndex()
            if (index >= 0) playQueueIndex(index)
        }
    }

    private suspend fun handleErrorRecoveryStep() {
        val waitMs = if (errorRecoveryAttempts == 0) {
            ERROR_RECOVERY_INITIAL_DELAY_MS
        } else {
            ERROR_RECOVERY_BETWEEN_ATTEMPTS_MS
        }
        delay(waitMs)

        if (audioPlayerEngine.state.value.errorMessage == null) {
            return
        }

        if (errorRecoveryAttempts >= MAX_ERROR_RECOVERY_ATTEMPTS) {
            if (queueController.canGoNext) {
                Log.d(
                    PLAYBACK_TAG,
                    "Agotados $MAX_ERROR_RECOVERY_ATTEMPTS intentos sin recuperacion, saltando a la siguiente cancion"
                )
                errorRecoveryAttempts = 0
                lastStablePlaybackStartMs = 0L
                goNext()
            }
            return
        }

        errorRecoveryAttempts++
        Log.d(
            PLAYBACK_TAG,
            "Intento de recuperacion $errorRecoveryAttempts/$MAX_ERROR_RECOVERY_ATTEMPTS"
        )
        lastStablePlaybackStartMs = 0L
        audioPlayerEngine.resume()
    }

    fun onAppForeground() {
        Log.d(PLAYBACK_TAG, "App en foreground, delegando lifecycle a Media3.")
    }

    fun onAppBackground() {
        Log.d(PLAYBACK_TAG, "App en background, delegando lifecycle a Media3.")
    }

    private fun acquireTransitionWakeLock() {
        if (!transitionWakeLock.isHeld) {
            transitionWakeLock.acquire(WAKELOCK_TIMEOUT_MS)
        }
    }

    private fun releaseTransitionWakeLock() {
        if (transitionWakeLock.isHeld) transitionWakeLock.release()
    }

    private fun startMediaService() {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.d(PLAYBACK_TAG, "MediaPlaybackService iniciado")
        } catch (e: Exception) {
            Log.e(PLAYBACK_TAG, "Error iniciando MediaPlaybackService: ${e.message}", e)
        }
    }

    private fun stopMediaService() {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java)
            context.stopService(intent)
            Log.d(PLAYBACK_TAG, "MediaPlaybackService detenido")
        } catch (e: Exception) {
            Log.e(PLAYBACK_TAG, "Error deteniendo MediaPlaybackService: ${e.message}", e)
        }
    }
}
