package com.essence.essenceapp.feature.playlist.ui.addsongs.extractor

import java.time.LocalDate

data class AddSongMetadata(
    val videoId: String,
    val title: String,
    val durationMs: Int,
    val uploaderName: String,
    val uploaderUrl: String,
    val thumbnailUrl: String?,
    val viewCount: Long,
    val releaseDate: LocalDate?
)
