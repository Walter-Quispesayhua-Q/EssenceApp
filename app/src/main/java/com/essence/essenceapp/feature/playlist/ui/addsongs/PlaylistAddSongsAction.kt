package com.essence.essenceapp.feature.playlist.ui.addsongs

sealed interface PlaylistAddSongsAction {
    data class QueryChanged(val value: String) : PlaylistAddSongsAction
    data object Submit : PlaylistAddSongsAction
    data class AddSong(val songKey: String) : PlaylistAddSongsAction
    data object LoadNextPage : PlaylistAddSongsAction
}
