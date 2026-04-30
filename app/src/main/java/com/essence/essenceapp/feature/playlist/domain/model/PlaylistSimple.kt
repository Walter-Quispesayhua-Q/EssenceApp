package com.essence.essenceapp.feature.playlist.domain.model

data class PlaylistSimple(
    val id: Long,
    val title: String,
    val isPublic: Boolean,
    val type: String = "NORMAL",
    val totalLikes: Long? = null
)