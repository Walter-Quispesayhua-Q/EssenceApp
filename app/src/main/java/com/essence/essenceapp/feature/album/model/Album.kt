package com.essence.essenceapp.feature.album.model

import com.essence.essenceapp.feature.artist.model.ArtistSimple
import com.essence.essenceapp.feature.song.model.SongSimple
import java.time.LocalDate

data class Album(
    val id: Long,
    val title: String,
    val description: String?,
    val imageKey: String?,
    val releaseDate: LocalDate?,
    val artists: List<ArtistSimple>,
    val songs: List<SongSimple>?
)
