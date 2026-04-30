package com.essence.essenceapp.feature.playlist.ui.detail

import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.song.domain.model.SongSimple

sealed interface PlaylistDetailUiState {

    data object Loading: PlaylistDetailUiState

    data object Deleted: PlaylistDetailUiState

    data class Error(
        val message: String
    ): PlaylistDetailUiState

    data class Success(
        val playlist: Playlist,
        val songs: List<SongSimple> = emptyList(),
        val isSongsLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val isLikeSubmitting: Boolean = false,
        val isDeleting: Boolean = false,
        val deleteError: String? = null
    ): PlaylistDetailUiState
}