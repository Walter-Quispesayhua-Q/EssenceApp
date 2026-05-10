package com.essence.essenceapp.core.extractor

import java.time.Instant
import java.time.LocalDate

data class ExtractedSong(
    val videoId: String,
    val title: String,
    val durationMs: Int,
    val uploaderName: String,
    val uploaderUrl: String,
    val thumbnailUrl: String?,
    val streamingUrl: String?,
    val streamingUrlExpiresAt: Instant?,
    val viewCount: Long,
    val releaseDate: LocalDate?
)