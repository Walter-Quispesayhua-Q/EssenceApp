package com.essence.essenceapp.feature.history.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HistoryApiDTO(
    val playlistId: Long?,
    val albumId: Long?,
    val durationListenedMs: Int?,
    val completed: Boolean?,
    val skipped: Boolean?,
    val skipPositionMs: Int?,
    val deviceType: String? = null
)
