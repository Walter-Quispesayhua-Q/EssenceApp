package com.essence.essenceapp.feature.home.data.mapper

import com.essence.essenceapp.feature.album.data.mapper.albumToSimpleDomain
import com.essence.essenceapp.feature.artist.data.mapper.artistToSimpleDomain
import com.essence.essenceapp.feature.home.data.dto.HomeResponseApiDTO
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain

fun HomeResponseApiDTO.homeToDomain(): Home? {
    return Home(
        songs = this.songs?.mapNotNull
        { it.songToSimpleDomain() } ?: emptyList(),
        albums = this.albums?.mapNotNull
        { it.albumToSimpleDomain() } ?: emptyList(),
        artists = this.artists?.mapNotNull
        { it.artistToSimpleDomain() } ?: emptyList(),
        status = this.status?.homeStatusToDomain() ?: return null
    )
}