package com.essence.essenceapp.feature.song.ui

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.manager.PlaybackUiState

sealed interface SongDetailUiState {
    data object Loading : SongDetailUiState
    data class Error(val message: String) : SongDetailUiState
    data class Success(
        val song: Song,
        val playback: PlaybackUiState,
        val isLikeSubmitting: Boolean = false
    ) : SongDetailUiState
}