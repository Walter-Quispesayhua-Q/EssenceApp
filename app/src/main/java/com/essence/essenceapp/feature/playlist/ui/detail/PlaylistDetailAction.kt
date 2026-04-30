package com.essence.essenceapp.feature.playlist.ui.detail

sealed interface PlaylistDetailAction {
    data object EditPlaylist : PlaylistDetailAction
    data object DeletePlaylist : PlaylistDetailAction
    data object AddSongs : PlaylistDetailAction
    data class RemoveSong(val songId: Long) : PlaylistDetailAction
    data class OpenSong(val songLookup: String) : PlaylistDetailAction
    data object ToggleLike : PlaylistDetailAction
}
