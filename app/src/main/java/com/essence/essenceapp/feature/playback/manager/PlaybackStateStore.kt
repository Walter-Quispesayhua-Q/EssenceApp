package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.playback.domain.NowPlayingInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackError
import com.essence.essenceapp.feature.playback.domain.PlaybackPositionInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackQueue
import com.essence.essenceapp.feature.playback.domain.PlaybackRepeatMode
import com.essence.essenceapp.feature.playback.domain.PlaybackShuffleMode
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.domain.PlaybackUiState
import com.essence.essenceapp.feature.song.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Guarda el estado interno y publico de playback.
 *
 * Centraliza los flows que observan la UI, el service y los observers para que
 * el controller principal no tenga que manejar cada MutableStateFlow a mano.
 */
@Singleton
class PlaybackStateStore @Inject constructor(
    @ApplicationScope scope: CoroutineScope
) {
    private val _nowPlaying = MutableStateFlow<NowPlayingInfo?>(null)
    val nowPlaying: StateFlow<NowPlayingInfo?> = _nowPlaying.asStateFlow()

    private val _queue = MutableStateFlow<PlaybackQueue?>(null)
    val queue: StateFlow<PlaybackQueue?> = _queue.asStateFlow()

    private val _position = MutableStateFlow(PlaybackPositionInfo.Zero)
    val position: StateFlow<PlaybackPositionInfo> = _position.asStateFlow()

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _repeatMode = MutableStateFlow(PlaybackRepeatMode.OFF)
    val repeatMode: StateFlow<PlaybackRepeatMode> = _repeatMode.asStateFlow()

    private val _shuffleMode = MutableStateFlow(PlaybackShuffleMode.OFF)
    val shuffleMode: StateFlow<PlaybackShuffleMode> = _shuffleMode.asStateFlow()

    private val _errors = MutableSharedFlow<PlaybackError>(extraBufferCapacity = 1)
    val errors: SharedFlow<PlaybackError> = _errors.asSharedFlow()

    val uiState: StateFlow<PlaybackUiState> =
        combine(
            _nowPlaying,
            _queue,
            _position,
            _playbackState,
            _repeatMode,
            _shuffleMode
        ) { values ->
            PlaybackUiState(
                nowPlaying = values[0] as NowPlayingInfo?,
                queue = values[1] as PlaybackQueue?,
                position = values[2] as PlaybackPositionInfo,
                playbackState = values[3] as PlaybackState,
                repeatMode = values[4] as PlaybackRepeatMode,
                shuffleMode = values[5] as PlaybackShuffleMode
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = PlaybackUiState.Empty
        )

    val currentQueue: PlaybackQueue?
        get() = _queue.value

    val currentItem
        get() = _queue.value?.current

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    val resolvedSong: Song?
        get() = _currentSong.value

    fun setQueue(queue: PlaybackQueue?) {
        _queue.value = queue
        updateNowPlaying()
    }

    fun setCurrentResolvedSong(song: Song?) {
        _currentSong.value = song
        updateNowPlaying()
    }

    fun clearCurrentResolvedSong() {
        _currentSong.value = null
        updateNowPlaying()
    }

    fun setPosition(position: PlaybackPositionInfo) {
        _position.value = position
    }

    fun resetPosition(positionMs: Long = 0L) {
        _position.value = PlaybackPositionInfo(
            positionMs = positionMs.coerceAtLeast(0L),
            durationMs = 0L,
            bufferedMs = 0L
        )
    }

    fun setPlaybackState(state: PlaybackState) {
        _playbackState.value = state
    }

    fun setRepeatMode(mode: PlaybackRepeatMode) {
        _repeatMode.value = mode
        updateNowPlaying()
    }

    fun setShuffleMode(mode: PlaybackShuffleMode) {
        _shuffleMode.value = mode
    }

    fun clearPlayback() {
        _currentSong.value = null
        _queue.value = null
        _nowPlaying.value = null
        _position.value = PlaybackPositionInfo.Zero
        _playbackState.value = PlaybackState.Idle
    }

    fun updateNowPlaying(isLikedOverride: Boolean? = null) {
        val queue = _queue.value
        val item = queue?.current

        _nowPlaying.value = if (queue == null || item == null) {
            null
        } else {
            NowPlayingInfo(
                item = item,
                isLiked = isLikedOverride ?: _currentSong.value
                    ?.takeIf { it.hlsMasterKey == item.hlsMasterKey }
                    ?.isLiked
                ?: false,
                canSkipNext = queue.hasNext || _repeatMode.value == PlaybackRepeatMode.ALL,
                canSkipPrevious = queue.hasPrevious
            )
        }
    }

    fun tryFail(error: PlaybackError) {
        _errors.tryEmit(error)
        _playbackState.value = PlaybackState.Failed(error)
    }

    suspend fun fail(error: PlaybackError) {
        _playbackState.value = PlaybackState.Failed(error)
        _errors.emit(error)
    }
}