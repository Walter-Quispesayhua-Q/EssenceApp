package com.essence.essenceapp.feature.song.data.dto

import com.essence.essenceapp.feature.album.model.AlbumResponseSimpleDTO
import com.essence.essenceapp.feature.artist.model.ArtistResponseSimpleDTO
import java.time.LocalDate

data class SongResponseApiDTO(
    val id: Long?,
    val title: String?,
    val durationMs: Int?,
    val hlsMasterKey: String?,
    val imageKey: String?,
    val songType: String?,
    val totalPlays: Int?,
    val artists: List<ArtistResponseSimpleDTO>?,
    val album: AlbumResponseSimpleDTO?,
    val releaseDate: LocalDate?
)
