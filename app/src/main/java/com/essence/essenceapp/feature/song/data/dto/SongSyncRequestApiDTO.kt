package com.essence.essenceapp.feature.song.data.dto

import java.time.LocalDate

data class SongSyncRequestApiDTO(
    val videoId: String,
    val title: String,
    val durationMs: Int,
    val uploaderName: String,
    val uploaderUrl: String,
    val thumbnailUrl: String?,
    val streamingUrl: String?,
    val viewCount: Long?,
    val releaseDate: LocalDate?
)
