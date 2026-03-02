package com.essence.essenceapp.feature.playlist.data.dto

data class PlaylistResponseSimpleApiDTO(
    val id: Long?,
    val title: String?,
    val isPublic: Boolean?,
    val totalLikes: Long? = null
)
