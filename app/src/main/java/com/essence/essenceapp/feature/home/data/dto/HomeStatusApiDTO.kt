package com.essence.essenceapp.feature.home.data.dto

data class HomeStatusApiDTO(
    val songsLoaded: Boolean?,
    val albumsLoaded: Boolean?,
    val artistsLoaded: Boolean?,
    val error: String?
)
