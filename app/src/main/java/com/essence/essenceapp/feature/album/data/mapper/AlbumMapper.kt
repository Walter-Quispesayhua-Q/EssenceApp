package com.essence.essenceapp.feature.album.data.mapper

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseApiDTO
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.artist.data.mapper.artistToSimpleDomain
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain

fun AlbumResponseApiDTO.albumToDomain(): Album? {
    return Album(
        id = this.id ?: return null,
        title = this.title ?: return null,
        description = this.description,
        imageKey = this.imageKey,
        releaseDate = this.releaseDate,
        artists = this.artists?.mapNotNull
        { it.artistToSimpleDomain() } ?: emptyList(),
        songs = this.songs?.mapNotNull
        { it.songToSimpleDomain() } ?: emptyList()
    )
}