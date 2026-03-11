package com.essence.essenceapp.feature.playlist.data.mapper

import com.essence.essenceapp.feature.playlist.data.dto.PlaylistsSimplesResponseApiDTO
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples

fun PlaylistsSimplesResponseApiDTO.playlistsSimplesToDomain(): PlaylistsSimples {
    return PlaylistsSimples(
        myPlaylists = this.myPlaylists?.mapNotNull { it.playlistToSimpleDomain() },
        playlistsPublic = this.playlistsPublic?.mapNotNull { it.playlistToSimpleDomain() }
    )
}