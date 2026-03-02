package com.essence.essenceapp.feature.playlist.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistRequestApiDTO(
    val title: String,
    val description: String? = null,
    val isPublic: Boolean
)