package com.essence.essenceapp.feature.song.ui.manager

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class PlaybackManager @Inject constructor() {

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    private val _nowPlaying = MutableStateFlow<NowPlayingInfo?>(null)
    val nowPlaying: StateFlow<NowPlayingInfo?> = _nowPlaying.asStateFlow()

    fun setNowPlaying(info: NowPlayingInfo) {
        _nowPlaying.value = info
    }

    fun clearNowPlaying() {
        _nowPlaying.value = null
        _uiState.value = PlaybackUiState()
    }

    fun onAction(action: SongDetailManagerAction) {
        when (action) {
            SongDetailManagerAction.Play -> _uiState.value = _uiState.value.copy(isPlaying = true)
            SongDetailManagerAction.Pause -> _uiState.value = _uiState.value.copy(isPlaying = false)
            SongDetailManagerAction.Stop -> {
                _uiState.value = _uiState.value.copy(
                    isPlaying = false,
                    positionMs = 0L
                )
                clearNowPlaying()
            }
            SongDetailManagerAction.Next -> Unit
            SongDetailManagerAction.Previous -> Unit
            is SongDetailManagerAction.SeekTo -> _uiState.value =
                _uiState.value.copy(positionMs = action.positionMs)
            is SongDetailManagerAction.SeekBy -> {
                val next = (_uiState.value.positionMs + action.deltaMs).coerceAtLeast(0L)
                _uiState.value = _uiState.value.copy(positionMs = next)
            }
            SongDetailManagerAction.ToggleRepeat -> _uiState.value =
                _uiState.value.copy(isRepeat = !_uiState.value.isRepeat)
        }
    }
}