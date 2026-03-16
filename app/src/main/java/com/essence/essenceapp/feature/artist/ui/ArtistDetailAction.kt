package com.essence.essenceapp.feature.artist.ui

sealed interface ArtistDetailAction {
    data object Back : ArtistDetailAction
    data object Refresh : ArtistDetailAction
    data class OpenSong(val songLookup: String) : ArtistDetailAction
    data class OpenAlbum(val albumLookup: String) : ArtistDetailAction
    data object ToggleLike : ArtistDetailAction
}