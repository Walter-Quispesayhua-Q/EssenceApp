package com.essence.essenceapp.feature.song.data.dto

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class SongResponseSimpleApiDTO(
    val id: Long?,
    val title: String?,
    val durationMs: Int?,
    val hlsMasterKey: String?,
    val imageKey: String?,
    val songType: String?,
    val totalPlays: Long?,
    val artistName: String?,
    val albumName: String?,
    val releaseDate: LocalDate?
)
