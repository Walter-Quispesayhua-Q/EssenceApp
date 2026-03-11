package com.essence.essenceapp.feature.playlist.data.dto

data class PlaylistsSimplesResponseApiDTO(
    val myPlaylists: List<PlaylistResponseSimpleApiDTO>?,
    val playlistsPublic: List<PlaylistResponseSimpleApiDTO>?
)
