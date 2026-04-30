package com.essence.essenceapp.feature.song.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.RefreshStreamingUrlUseCase
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.shared.cache.QueueCache
import com.essence.essenceapp.shared.cache.SongDetailCache
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import com.essence.essenceapp.feature.song.ui.playback.model.NowPlayingInfo


@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val refreshStreamingUrlUseCase: RefreshStreamingUrlUseCase,
    private val playbackManager: PlaybackManager,
    private val addLikeSongUseCase: AddLikeSongUseCase,
    private val deleteLikeSongUseCase: DeleteLikeSongUseCase,
    private val queueCache: QueueCache,
    private val songDetailCache: SongDetailCache
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongDetailUiState>(SongDetailUiState.Loading)
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()

    private var currentSongLookup: String? = null
    private var fetchJob: Job? = null


    init {
        observePlaybackState()
        observeQueue()
        observeNowPlaying()
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            playbackManager.uiState.collect { playback ->
                when (val current = _uiState.value) {
                    is SongDetailUiState.Success ->
                        _uiState.value = current.copy(playback = playback)
                    is SongDetailUiState.LoadingNextSong ->
                        _uiState.value = current.copy(playback = playback)
                    else -> Unit
                }
            }
        }
    }

    private fun observeQueue() {
        viewModelScope.launch {
            playbackManager.queue.collect { queue ->
                val current = _uiState.value
                if (current is SongDetailUiState.Success) {
                    _uiState.value = current.copy(
                        queueItems = queue?.items ?: emptyList(),
                        queueCurrentIndex = queue?.currentIndex ?: -1
                    )
                }
            }
        }
    }

    private fun observeNowPlaying() {
        viewModelScope.launch {
            playbackManager.nowPlaying.drop(1).collect { info ->
                val lookup = info?.songLookup ?: return@collect
                if (lookup == currentSongLookup) return@collect

                currentSongLookup = lookup
                fetchJob?.cancel()

                val detailCached = songDetailCache.get(lookup)
                if (detailCached != null) {
                    showSuccess(detailCached)
                    playbackManager.playSong(detailCached, forceRestart = false)
                    return@collect
                }

                val resolved = playbackManager.getResolvedSong(lookup)
                if (resolved != null) {
                    songDetailCache.put(lookup, resolved)
                    showSuccess(resolved)
                    return@collect
                }

                showLoadingForNextSong(info)
                fetchSong(lookup = lookup, forceRestart = false)
            }
        }
    }

    // ACCIONES PÚBLICAS
    fun loadSong(lookup: String) {
        currentSongLookup = lookup

        val detailCached = songDetailCache.get(lookup)
        if (detailCached != null) {
            showSuccess(detailCached)
            playbackManager.playSong(detailCached, forceRestart = false)
            return
        }

        val queueCached = queueCache.findItem(lookup)
        if (queueCached != null) {
            _uiState.value = SongDetailUiState.LoadingNextSong(
                title = queueCached.title,
                artistName = queueCached.artistName,
                imageKey = queueCached.imageKey,
                durationMs = queueCached.durationMs.toLong(),
                playback = playbackManager.uiState.value.copy(
                    durationMs = queueCached.durationMs.toLong()
                )
            )
        } else {
            _uiState.value = SongDetailUiState.Loading
        }

        fetchSong(lookup = lookup, forceRestart = false)
    }

    fun onAction(action: SongDetailAction) {
        when (action) {
            SongDetailAction.Back -> Unit
            SongDetailAction.Refresh -> currentSongLookup?.let {
                fetchSong(lookup = it, forceRestart = true)
            }
            is SongDetailAction.OpenAlbum -> Unit
            is SongDetailAction.OpenArtist -> Unit
            SongDetailAction.AddToPlaylist -> Unit
            SongDetailAction.ToggleLike -> toggleLike()
            is SongDetailAction.PlayQueueItem -> playbackManager.playQueueIndex(action.index)
        }
    }

    fun onPlaybackAction(action: PlaybackAction) {
        playbackManager.onAction(action)
    }

    //  OBTENER CANCIÓN

    private fun fetchSong(lookup: String, forceRestart: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val result = getSongUseCase(lookup)

                if (lookup != currentSongLookup) return@launch

                result.onSuccess { song ->
                    val songToPlay = refreshIfExpired(song)

                    // Re-validar despues del refresh: el usuario pudo cambiar
                    // de cancion mientras esperabamos.
                    if (lookup != currentSongLookup) return@launch

                    if (songToPlay.streamingUrl.isNullOrBlank()) {
                        handleUnavailableSong(songToPlay, lookup)
                        return@launch
                    }

                    songDetailCache.put(lookup, songToPlay)
                    showSuccess(songToPlay)
                    playbackManager.playSong(songToPlay, forceRestart = forceRestart)
                }

                result.onFailure { error ->
                    _uiState.value = SongDetailUiState.Error(error.toUserMessage())
                }
            } catch (error: Exception) {
                if (lookup == currentSongLookup) {
                    _uiState.value = SongDetailUiState.Error(error.toUserMessage())
                }
            }
        }
    }

    private suspend fun refreshIfExpired(
        song: com.essence.essenceapp.feature.song.domain.model.Song
    ): com.essence.essenceapp.feature.song.domain.model.Song {
        val urlBlank = song.streamingUrl.isNullOrBlank()
        val expiredByTimestamp = song.streamingUrlExpiresAt?.isBefore(Instant.now()) ?: false
        val needsRefresh = urlBlank || expiredByTimestamp

        if (!needsRefresh) return song

        val videoId = song.hlsMasterKey
        val refreshResult = refreshStreamingUrlUseCase(
            currentSong = song,
            isStillCurrent = { currentSongLookup == videoId }
        )
        return refreshResult.getOrDefault(song)
    }

    // like

    private fun toggleLike() {
        val current = _uiState.value as? SongDetailUiState.Success ?: return
        if (current.isLikeSubmitting) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLikeSubmitting = true)

            val result = try {
                if (current.song.isLiked) {
                    deleteLikeSongUseCase(current.song.id)
                } else {
                    addLikeSongUseCase(current.song.id)
                }
            } catch (error: Exception) {
                Result.failure(error)
            }

            result.onSuccess {
                val latest = _uiState.value as? SongDetailUiState.Success ?: return@onSuccess
                val updatedSong = latest.song.copy(isLiked = !current.song.isLiked)
                // Actualizar también el cache
                currentSongLookup?.let { songDetailCache.put(it, updatedSong) }
                _uiState.value = latest.copy(
                    song = updatedSong,
                    isLikeSubmitting = false
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }

    // HELPERS

    private fun showSuccess(song: com.essence.essenceapp.feature.song.domain.model.Song) {
        val queue = playbackManager.queue.value
        _uiState.value = SongDetailUiState.Success(
            song = song,
            playback = playbackManager.uiState.value,
            isLikeSubmitting = false,
            queueItems = queue?.items ?: emptyList(),
            queueCurrentIndex = queue?.currentIndex ?: -1
        )
    }

    private fun showLoadingForNextSong(info: NowPlayingInfo) {
        _uiState.value = SongDetailUiState.LoadingNextSong(
            title = info.title,
            artistName = info.artistName,
            imageKey = info.imageKey,
            durationMs = info.durationMs,
            playback = playbackManager.uiState.value.copy(
                durationMs = info.durationMs
            )
        )
    }

    private suspend fun handleUnavailableSong(
        song: com.essence.essenceapp.feature.song.domain.model.Song,
        lookup: String
    ) {
        _uiState.value = SongDetailUiState.Unavailable(songTitle = song.title)
        delay(3_000L)
        if (lookup == currentSongLookup) {
            playbackManager.onAction(PlaybackAction.Next)
        }
    }
}