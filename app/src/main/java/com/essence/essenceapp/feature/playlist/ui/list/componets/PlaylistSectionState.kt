package com.essence.essenceapp.feature.playlist.ui.list.componets

import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListUiState

internal sealed interface PlaylistSectionState {
    data object Loading : PlaylistSectionState
    data class Error(val message: String) : PlaylistSectionState
    data class Data(val items: List<PlaylistSimple>) : PlaylistSectionState
}

internal fun PlaylistListUiState.toSections(): Pair<PlaylistSectionState, PlaylistSectionState> =
    when (this) {
        is PlaylistListUiState.Idle,
        is PlaylistListUiState.Loading ->
            PlaylistSectionState.Loading to PlaylistSectionState.Loading

        is PlaylistListUiState.Error ->
            PlaylistSectionState.Error(message) to PlaylistSectionState.Error(message)

        is PlaylistListUiState.Success ->
            PlaylistSectionState.Data(playlist.myPlaylists.orEmpty()) to
                    PlaylistSectionState.Data(playlist.playlistsPublic.orEmpty())
    }

internal fun PlaylistSectionState.counterText(): String =
    when (this) {
        PlaylistSectionState.Loading -> "..."
        is PlaylistSectionState.Error -> "--"
        is PlaylistSectionState.Data -> items.size.toString()
    }
