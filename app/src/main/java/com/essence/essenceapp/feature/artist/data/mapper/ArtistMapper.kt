package com.essence.essenceapp.feature.artist.data.mapper

import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseApiDTO
import com.essence.essenceapp.feature.artist.model.Artist
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain

fun ArtistResponseApiDTO.artistToDomain(): Artist? {
    return Artist(
        id = this.id ?: return null,
        nameArtist = this.nameArtist ?: return null,
        description = this.description,
        imageKey = this.imageKey,
        artistUrl = this.artistUrl ?: return null,
        country = this.country,
        songs = this.songs?.mapNotNull
        { it.songToSimpleDomain() } ?: emptyList(),
        albums = this.albums?.mapNotNull
        { it.artistToSimpleDomain() } ?: emptyList()

    )
}