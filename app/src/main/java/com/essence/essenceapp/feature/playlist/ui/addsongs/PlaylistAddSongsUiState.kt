package com.essence.essenceapp.feature.playlist.ui.addsongs

import com.essence.essenceapp.feature.song.domain.model.SongSimple

sealed interface PlaylistAddSongsUiState {

    data object Idle : PlaylistAddSongsUiState

    data object Loading : PlaylistAddSongsUiState

    data class Success(
        val query: String = "",
        val songs: List<SongSimple> = emptyList(),
        val addedSongKeys: Set<String> = emptySet(),
        val addingSongKeys: Set<String> = emptySet(),
        val hasNextPage: Boolean = false,
        val isLoadingNextPage: Boolean = false
    ) : PlaylistAddSongsUiState

    data class Error(val message: String) : PlaylistAddSongsUiState
}
