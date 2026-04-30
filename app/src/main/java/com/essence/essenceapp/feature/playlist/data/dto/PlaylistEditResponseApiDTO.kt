package com.essence.essenceapp.feature.playlist.data.dto

data class PlaylistEditResponseApiDTO(
    val id: Long?,
    val title: String?,
    val description: String?,
    val imageKey: String?,
    val isPublic: Boolean?,
    val type: String? = null
)
