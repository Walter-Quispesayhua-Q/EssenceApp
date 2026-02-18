package com.essence.essenceapp.feature.song.model

import java.time.LocalDate

data class SongSimple(
    val id: Long,
    val durationMs: Int,
    val hlsMasterKey: String,
    val imageKey: String,
    val songType: String,
    val totalPlays: Long,
    val artistName: String,
    val albumName: String,
    val releaseDate: LocalDate
)
