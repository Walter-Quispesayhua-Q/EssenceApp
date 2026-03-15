package com.essence.essenceapp.feature.home.ui

import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.song.domain.model.SongSimple

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Success(
        val homeData: Home,
        val recentSongs: List<SongSimple> = emptyList()
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}