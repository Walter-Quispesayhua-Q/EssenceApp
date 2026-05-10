package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.model.SongLookupHint
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.ui.playback.AudioPlayerState
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.PlaybackRepeatMode
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioOutputDetector
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioPlayerEngine
import com.essence.essenceapp.feature.song.ui.playback.mapper.toNowPlayingInfo
import com.essence.essenceapp.feature.song.ui.playback.model.NowPlayingInfo
import com.essence.essenceapp.shared.playback.mapper.toLookupHint
import com.essence.essenceapp.shared.playback.model.PlaybackQueue
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val PLAYBACK_TAG = "PLAYBACK_DEBUG"
private const val RESTART_THRESHOLD_MS = 3_000L

@Singleton
class PlaybackManager @Inject constructor(
    private val audioPlayerEngine: AudioPlayerEngine,
    private val audioOutputDetector: AudioOutputDetector,
    private val getSongUseCase: GetSongUseCase,
    private val queueController: PlaybackQueueController,
    private val historyRecorder: PlaybackHistoryRecorder,
    private val likeController: PlaybackLikeController,
    private val urlRefresher: PlaybackUrlRefresher,
    private val prefetchCoordinator: PlaybackPrefetchCoordinator,
    private val errorRecovery: PlaybackErrorRecoveryController,
    private val navigationController: PlaybackNavigationController,
    private val ttfpTracker: PlaybackTtfpTracker,
    private val transitionWakeLock: PlaybackTransitionWakeLock,
    private val mediaServiceController: PlaybackMediaServiceController,
    private val resolvedCache: ResolvedSongCache
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _nowPlaying = MutableStateFlow<NowPlayingInfo?>(null)
    val nowPlaying: StateFlow<NowPlayingInfo?> = _nowPlaying.asStateFlow()

    val queue: StateFlow<PlaybackQueue?> = queueController.queue

    val isCurrentSongLiked: StateFlow<Boolean> = likeController.isLiked

    private val _isResolvingNextSong = MutableStateFlow(false)
    private val _manualErrorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PlaybackUiState> = createPlaybackUiStateFlow(
        scope = scope,
        audioPlayerState = audioPlayerEngine.state,
        queue = queueController.queue,
        audioOutputType = audioOutputDetector.outputType,
        isResolvingNextSong = _isResolvingNextSong,
        manualErrorMessage = _manualErrorMessage
    )

    private var hasEndedHandled = false
    private var lastPositionMs: Long = 0L
    private var resolveSongJob: Job? = null
    private var sourceRefreshJob: Job? = null

    @Volatile
    private var pauseRequestedDuringLoad: Boolean = false

    fun getResolvedSong(lookup: String): Song? = resolvedCache.get(lookup)

    fun currentVideoId(): String? = _nowPlaying.value?.songLookup

    init {
        scope.launch {
            audioPlayerEngine.state.collect { audioState ->
                handlePlayerState(audioState)
            }
        }
    }

    private fun handlePlayerState(audioState: AudioPlayerState) {
        lastPositionMs = audioState.positionMs
        recordHistoryIfNeeded(audioState)
        autoAdvanceIfEnded(audioState)
        handleSourceRefreshRequest(audioState)
        manageErrorRecovery(audioState)
    }

    private fun recordHistoryIfNeeded(audioState: AudioPlayerState) {
        if (
            !historyRecorder.isAlreadyRecorded() &&
            historyRecorder.hasReachedListenThreshold(lastPositionMs)
        ) {
            _nowPlaying.value?.let { info ->
                historyRecorder.recordListened(info.songId, lastPositionMs)
            }
        }
        if (audioState.hasEnded && !historyRecorder.isAlreadyRecorded()) {
            _nowPlaying.value?.let { info ->
                historyRecorder.recordCompleted(info.songId, lastPositionMs)
            }
        }
    }

    private fun autoAdvanceIfEnded(audioState: AudioPlayerState) {
        if (
            audioState.hasEnded &&
            audioState.repeatMode == PlaybackRepeatMode.Off &&
            !hasEndedHandled
        ) {
            hasEndedHandled = true
            transitionWakeLock.acquire()
            goNext()
        }
    }

    private fun handleSourceRefreshRequest(audioState: AudioPlayerState) {
        if (!audioState.requiresSourceRefresh) return
        if (sourceRefreshJob?.isActive == true) return
        val info = _nowPlaying.value
        if (info == null || info.songLookup.isBlank()) {
            audioPlayerEngine.clearSourceRefreshRequest()
            return
        }
        audioPlayerEngine.clearSourceRefreshRequest()
        sourceRefreshJob = scope.launch {
            handleSourceRefresh(info.songLookup)
        }.also { job ->
            job.invokeOnCompletion {
                if (sourceRefreshJob === job) sourceRefreshJob = null
            }
        }
    }

    private fun manageErrorRecovery(audioState: AudioPlayerState) {
        val shouldAttemptRecovery = audioState.errorMessage != null &&
                !audioState.requiresSourceRefresh &&
                audioState.repeatMode == PlaybackRepeatMode.Off &&
                queueController.canGoNext

        if (shouldAttemptRecovery) {
            errorRecovery.scheduleStep(
                isErrorStillPresent = { audioPlayerEngine.state.value.errorMessage != null },
                canGoNext = { queueController.canGoNext },
                onRetry = { audioPlayerEngine.resume() },
                onSkipToNext = { goNext() }
            )
            return
        }

        errorRecovery.cancel()
        if (audioState.isPlaying) {
            errorRecovery.noteStablePlayback()
            ttfpTracker.reportIfPending(_nowPlaying.value?.songLookup)
        } else {
            errorRecovery.noteNoPlayback()
        }
    }

    private fun recordSkippedForCurrentIfNeeded(nextLookup: String) {
        val current = _nowPlaying.value ?: return
        if (current.songLookup == nextLookup) return
        if (historyRecorder.isAlreadyRecorded()) return
        if (!historyRecorder.shouldRecordOnSwitch(lastPositionMs)) return
        historyRecorder.recordSkipped(current.songId, lastPositionMs)
    }

    fun setQueue(queue: PlaybackQueue) = queueController.setQueue(queue)

    fun setQueueFromItems(
        items: List<PlaybackQueueItem>,
        startIndex: Int,
        sourceKey: String
    ) = queueController.setQueueFromItems(items, startIndex, sourceKey)

    fun clearQueue() = queueController.clear()


    fun playSong(song: Song, forceRestart: Boolean = false) {
        val info = song.toNowPlayingInfo()
        if (info == null) {
            if (song.streamingUrl.isNullOrBlank()) {
                Log.d(PLAYBACK_TAG, "Backend declaro URL nula para ${song.hlsMasterKey}, refresh certero")
                forceRefreshAndPlay(song, forceRestart)
            } else {
                Log.w(PLAYBACK_TAG, "streamingUrl ausente para ${song.hlsMasterKey} (transitorio)")
            }
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
        ttfpTracker.start(info.songLookup, "playSong", replaceExisting = forceRestart)

        resolvedCache.put(song.hlsMasterKey, song)
        queueController.alignIndex(info.songLookup)

        _manualErrorMessage.value = null
        _nowPlaying.value = info
        historyRecorder.resetForNewSong()
        hasEndedHandled = false
        lastPositionMs = 0L
        errorRecovery.reset()
        likeController.setLiked(song.isLiked)

        audioPlayerEngine.play(
            url = info.streamingUrl,
            forceRestart = forceRestart,
            title = info.title,
            artist = info.artistName,
            artworkUri = resolveImageUrl(info.imageKey),
            mediaId = info.songLookup
        )
        if (pauseRequestedDuringLoad) {
            pauseRequestedDuringLoad = false
            audioPlayerEngine.pause()
        }
        mediaServiceController.start()
        prefetchCoordinator.prefetchNext(queueController.peekNext())
        urlRefresher.scheduleProactive(song) { fresh ->
            resolvedCache.put(fresh.hlsMasterKey, fresh)
        }
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
        prefetchCoordinator.cancelAll()
        audioPlayerEngine.stop()
        errorRecovery.reset()
        _manualErrorMessage.value = null
        _nowPlaying.value = null
        mediaServiceController.stop()
    }

    fun onAction(action: PlaybackAction) {
        when (action) {
            PlaybackAction.Play -> {
                pauseRequestedDuringLoad = false
                if (uiState.value.errorMessage != null) {
                    replayCurrentSong()
                } else {
                    audioPlayerEngine.resume()
                }
            }
            PlaybackAction.Pause -> {
                if (uiState.value.isBuffering) {
                    pauseRequestedDuringLoad = true
                }
                audioPlayerEngine.pause()
            }
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
        prefetchCoordinator.cancelAll()
        audioPlayerEngine.release()
        _nowPlaying.value = null
        queueController.clear()
        resolvedCache.clear()
        ttfpTracker.clear()
    }

    fun goNext() {
        val nextItem = queueController.advanceToNext()
        if (nextItem == null) {
            Log.d(PLAYBACK_TAG, "goNext: fin de cola")
            transitionWakeLock.release()
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
        recordSkippedForCurrentIfNeeded(item.songLookup)

        _manualErrorMessage.value = null
        _isResolvingNextSong.value = true
        _nowPlaying.value = buildPlaceholderNowPlaying(item)

        navigationController.navigate { playQueueItem(item) }
    }

    fun playQueueIndex(index: Int) {
        val item = queueController.moveToIndex(index) ?: return
        playQueueItem(item)
    }

    private fun playQueueItem(item: PlaybackQueueItem) {
        ttfpTracker.start(item.songLookup, "queue")
        Log.d(PLAYBACK_TAG, "playQueueItem: lookup=${item.songLookup}")

        hasEndedHandled = false
        transitionWakeLock.acquire()
        _manualErrorMessage.value = null

        val cached = resolvedCache.get(item.songLookup)
        if (cached != null && !urlRefresher.isExpired(cached)) {
            _isResolvingNextSong.value = false
            mediaServiceController.start()
            playSong(cached, forceRestart = true)
            transitionWakeLock.release()
            return
        }

        _isResolvingNextSong.value = true
        _nowPlaying.value = buildPlaceholderNowPlaying(item, cached)

        resolveSongJob?.cancel()
        resolveSongJob = scope.launch {
            try {
                val result = if (cached != null) {
                    urlRefresher.refreshIfExpired(cached) { currentVideoId() == item.songLookup }
                } else {
                    getSongUseCase(item.songLookup, item.toLookupHint())
                }
                result.onSuccess { song ->
                    _isResolvingNextSong.value = false
                    resolvedCache.put(item.songLookup, song)
                    mediaServiceController.start()
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
                transitionWakeLock.release()
            }
        }.also { job ->
            job.invokeOnCompletion {
                if (resolveSongJob === job) resolveSongJob = null
            }
        }
    }

    private fun forceRefreshAndPlay(song: Song, forceRestart: Boolean) {
        val videoId = song.hlsMasterKey
        _isResolvingNextSong.value = true
        scope.launch {
            try {
                val result = urlRefresher.forceRefresh(song) {
                    currentVideoId() == videoId
                }
                result.onSuccess { fresh ->
                    _isResolvingNextSong.value = false
                    resolvedCache.put(fresh.hlsMasterKey, fresh)
                    playSong(fresh, forceRestart)
                }
                result.onFailure { error ->
                    Log.e(PLAYBACK_TAG, "Force refresh fallo $videoId: ${error.message}")
                    _isResolvingNextSong.value = false
                    _manualErrorMessage.value = "No se pudo cargar la cancion."
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e(PLAYBACK_TAG, "Force refresh exception $videoId: ${e.message}", e)
                _isResolvingNextSong.value = false
                _manualErrorMessage.value = "No se pudo cargar la cancion."
            }
        }
    }

    private suspend fun handleSourceRefresh(lookup: String) {
        Log.d(PLAYBACK_TAG, "URL expirada detectada por player, refrescando: $lookup")
        transitionWakeLock.acquire()
        _manualErrorMessage.value = null
        _isResolvingNextSong.value = true
        try {
            val currentSong = resolvedCache.get(lookup)
            val result = if (currentSong != null) {
                urlRefresher.forceRefresh(currentSong) { currentVideoId() == lookup }
            } else {
                val queueHint = queueController.current()
                    ?.takeIf { it.songLookup == lookup }
                    ?.toLookupHint()
                    ?: SongLookupHint.Unknown
                getSongUseCase(lookup, queueHint)
            }
            result.onSuccess { song ->
                _isResolvingNextSong.value = false
                resolvedCache.put(lookup, song)
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
            transitionWakeLock.release()
        }
    }

    private fun replayCurrentSong() {
        val info = _nowPlaying.value ?: return
        val song = resolvedCache.get(info.songLookup)
        if (song != null) {
            if (urlRefresher.isExpired(song)) {
                forceRefreshAndPlay(song, forceRestart = true)
            } else {
                playSong(song, forceRestart = true)
            }
            return
        }
        val index = queueController.currentIndex()
        if (index >= 0) playQueueIndex(index)
    }

    private fun buildPlaceholderNowPlaying(
        item: PlaybackQueueItem,
        cached: Song? = resolvedCache.get(item.songLookup)
    ): NowPlayingInfo = NowPlayingInfo(
        songId = cached?.id ?: item.songId ?: 0L,
        songLookup = item.songLookup,
        title = item.title,
        artistName = item.artistName,
        imageKey = item.imageKey,
        durationMs = item.durationMs,
        streamingUrl = ""
    )

    fun onAppForeground() {
        Log.d(PLAYBACK_TAG, "App en foreground, delegando lifecycle a Media3.")
    }

    fun onAppBackground() {
        Log.d(PLAYBACK_TAG, "App en background, delegando lifecycle a Media3.")
    }
}
