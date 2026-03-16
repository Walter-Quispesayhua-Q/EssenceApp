package com.essence.essenceapp.feature.artist.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistResponseSimpleApiDTO(
    val id: Long?,
    val nameArtist: String?,
    val imageKey: String?,
    val artistUrl: String?
)
