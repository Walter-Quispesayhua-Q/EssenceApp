package com.essence.essenceapp.feature.playlist.domain.model

import java.time.LocalDate

data class Playlist(
    val id: Long,
    val title: String,
    val description: String?,
    val imageKey: String?,
    val isPublic: Boolean,
    val totalSongs: Int,
    val createdAt: LocalDate,
    val updatedAt: LocalDate?,
    val totalLikes: Long? = null,
    val isLiked: Boolean = false
)