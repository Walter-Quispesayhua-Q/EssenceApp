package com.essence.essenceapp.feature.song.data.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDate

data class SongSyncRequestApiDTO(
    @SerializedName(value = "videoId", alternate = ["hlsMasterKey"])
    val hlsMasterKey: String,
    val title: String,
    val durationMs: Int,
    val uploaderName: String,
    val uploaderUrl: String,
    val thumbnailUrl: String?,
    val streamingUrl: String?,
    val streamingUrlExpiresAt: Instant?,
    val viewCount: Long?,
    val releaseDate: LocalDate?
)
