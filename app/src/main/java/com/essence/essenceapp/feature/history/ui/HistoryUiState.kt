package com.essence.essenceapp.feature.history.ui

import com.essence.essenceapp.feature.song.domain.model.SongSimple

sealed interface HistoryUiState {
    data object Loading : HistoryUiState

    data class Error(
        val message: String
    ) : HistoryUiState

    data class Success(
        val songs: List<SongSimple>
    ) : HistoryUiState
}
