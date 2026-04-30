package com.essence.essenceapp.feature.song.ui

sealed interface SongDetailAction {
    data object Back : SongDetailAction
    data object Refresh : SongDetailAction
    data class OpenArtist(val artistLookup: String) : SongDetailAction
    data class OpenAlbum(val albumLookup: String) : SongDetailAction
    data object ToggleLike : SongDetailAction
    data object AddToPlaylist : SongDetailAction
    data class PlayQueueItem(val index: Int) : SongDetailAction
}