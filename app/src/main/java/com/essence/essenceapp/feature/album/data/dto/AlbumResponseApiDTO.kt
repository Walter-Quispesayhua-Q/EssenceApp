package com.essence.essenceapp.feature.album.data.dto

import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseSimpleApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import java.time.LocalDate

data class AlbumResponseApiDTO(
    val id: Long?,
    val title: String?,
    val description: String?,
    val imageKey: String?,
    val releaseDate: LocalDate?,

    val artists: List<ArtistResponseSimpleApiDTO>?,
    val songs: List<SongResponseSimpleApiDTO>?
)
