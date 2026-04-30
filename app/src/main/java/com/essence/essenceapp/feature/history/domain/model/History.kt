package com.essence.essenceapp.feature.history.domain.model

data class History(
    val playlistId: Long?,
    val albumId: Long?,
    val durationListenedMs: Int,
    val completed: Boolean,
    val skipped: Boolean,
    val skipPositionMs: Int?,
    val deviceType: String? = null
)
