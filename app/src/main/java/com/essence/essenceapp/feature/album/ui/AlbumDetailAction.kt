package com.essence.essenceapp.feature.album.ui


sealed interface AlbumDetailAction {
    data object Back : AlbumDetailAction
    data object Refresh : AlbumDetailAction
    data class OpenSong(val songId: Long) : AlbumDetailAction
    data object ToggleLike : AlbumDetailAction
}