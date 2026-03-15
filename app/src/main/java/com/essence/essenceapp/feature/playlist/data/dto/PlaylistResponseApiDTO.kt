package com.essence.essenceapp.feature.playlist.data.dto

import java.time.LocalDate

data class PlaylistResponseApiDTO(
    val id: Long?,
    val title: String?,
    val description: String?,
    val imageKey: String?,
    val isPublic: Boolean?,
    val totalSongs: Int?,
    val createdAt: LocalDate?,
    val updatedAt: LocalDate?,
    val totalLikes: Long? = null,
    val isLiked: Boolean? = null
)
