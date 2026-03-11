package com.essence.essenceapp.feature.playlist.ui.list

import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples

sealed interface PlaylistListUiState {

    data object Idle: PlaylistListUiState

    data object Loading: PlaylistListUiState

    data class Error(
        val message: String
    ): PlaylistListUiState

    data class Success(
        val playlist: PlaylistsSimples
    ): PlaylistListUiState
}