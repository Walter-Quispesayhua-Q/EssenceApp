package com.essence.essenceapp.feature.home.domain.model

data class HomeStatus(
    val songsLoaded: Boolean,
    val albumsLoaded: Boolean,
    val artistsLoaded: Boolean,
    val error: String?
)
