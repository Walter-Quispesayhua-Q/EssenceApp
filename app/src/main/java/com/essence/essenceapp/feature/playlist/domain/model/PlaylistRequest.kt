package com.essence.essenceapp.feature.playlist.domain.model

data class PlaylistRequest(
    val title: String,
    val description: String? = null,
    val isPublic: Boolean
)
