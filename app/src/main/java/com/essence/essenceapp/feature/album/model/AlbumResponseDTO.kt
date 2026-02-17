package com.essence.essenceapp.feature.album.model

import com.essence.essenceapp.feature.artist.model.ArtistResponseSimpleDTO
import com.essence.essenceapp.feature.song.model.SongResponseSimpleDTO
import java.time.LocalDate

data class AlbumResponseDTO(
    val id: Long,
    val title: String,
    val description: String,
    val imageKey: String,
    val releaseDate: LocalDate,

    val artists: List<ArtistResponseSimpleDTO>,
    val songs: List<SongResponseSimpleDTO>
)
