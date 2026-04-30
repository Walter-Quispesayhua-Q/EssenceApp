package com.essence.essenceapp.feature.playlist.data.dto

data class PlaylistResponseSimpleApiDTO(
    val id: Long?,
    val title: String?,
    val isPublic: Boolean?,
    val type: String? = null,
    val totalLikes: Long? = null
)
