package com.essence.essenceapp.feature.artist.domain.model

data class ArtistSimple(
    val id: Long,
    val nameArtist: String,
    val imageKey: String?,
    val artistUrl: String
)
