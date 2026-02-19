package com.essence.essenceapp.feature.song.data.dto

import java.time.LocalDate

data class SongResponseSimpleApiDTO(
    val id: Long?,
    val durationMs: Int?,
    val hlsMasterKey: String?,
    val imageKey: String?,
    val songType: String?,
    val totalPlays: Long?,
    val artistName: String?,
    val albumName: String?,
    val releaseDate: LocalDate?
)
