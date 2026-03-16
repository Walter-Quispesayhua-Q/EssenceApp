package com.essence.essenceapp.feature.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeStatusApiDTO(
    val songsLoaded: Boolean?,
    val albumsLoaded: Boolean?,
    val artistsLoaded: Boolean?,
    val error: String?
)
