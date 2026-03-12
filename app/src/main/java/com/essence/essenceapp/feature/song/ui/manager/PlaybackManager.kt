package com.essence.essenceapp.feature.song.ui.manager

import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class PlaybackManager @Inject constructor() {

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    fun onAction(action: SongDetailManagerAction) {
        when (action) {
            SongDetailManagerAction.Play -> _uiState.value = _uiState.value.copy(isPlaying = true)
            SongDetailManagerAction.Pause -> _uiState.value = _uiState.value.copy(isPlaying = false)
            SongDetailManagerAction.Stop -> _uiState.value = _uiState.value.copy(
                isPlaying = false,
                positionMs = 0L
            )
            SongDetailManagerAction.Next -> Unit
            SongDetailManagerAction.Previous -> Unit
            is SongDetailManagerAction.SeekTo -> _uiState.value =
                _uiState.value.copy(positionMs = action.positionMs)

            is SongDetailManagerAction.SeekBy -> {
                val next = (_uiState.value.positionMs + action.deltaMs).coerceAtLeast(0L)
                _uiState.value = _uiState.value.copy(positionMs = next)
            }
        }
    }

}
