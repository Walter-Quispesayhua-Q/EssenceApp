package com.essence.essenceapp.feature.song.ui

sealed interface SongDetailAction {
    data object Back : SongDetailAction
    data object Refresh : SongDetailAction
    data class OpenArtist(val artistId: Long) : SongDetailAction
    data class OpenAlbum(val albumId: Long) : SongDetailAction
}