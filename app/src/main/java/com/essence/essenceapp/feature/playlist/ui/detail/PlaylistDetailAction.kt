package com.essence.essenceapp.feature.playlist.ui.detail

sealed interface PlaylistDetailAction {
    data object EditPlaylist : PlaylistDetailAction
    data object DeletePlaylist : PlaylistDetailAction
    data class RemoveSong(val songId: Long) : PlaylistDetailAction
}
