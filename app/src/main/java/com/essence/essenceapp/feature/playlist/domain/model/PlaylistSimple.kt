package com.essence.essenceapp.feature.playlist.domain.model

data class PlaylistSimple(
    val id: Long,
    val title: String,
    val isPublic: Boolean,
    val totalLikes: Long? = null
)