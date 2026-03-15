package com.essence.essenceapp.feature.artist.ui

sealed interface ArtistDetailAction {
    data object Back : ArtistDetailAction
    data object Refresh : ArtistDetailAction
    data class OpenSong(val songId: Long) : ArtistDetailAction
    data class OpenAlbum(val albumId: Long) : ArtistDetailAction
    data object ToggleLike : ArtistDetailAction
}
