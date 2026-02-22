package com.essence.essenceapp.feature.artist.data.mapper

import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseSimpleApiDTO
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple

fun ArtistResponseSimpleApiDTO.artistToSimpleDomain(): ArtistSimple? {
    return ArtistSimple(
        id = this.id ?: return null,
        nameArtist = this.nameArtist ?: return null,
        imageKey = this.imageKey,
        artistUrl = this.artistUrl ?: return null
    )
}