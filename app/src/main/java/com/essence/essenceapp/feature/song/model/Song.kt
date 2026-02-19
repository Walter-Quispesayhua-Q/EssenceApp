package com.essence.essenceapp.feature.song.model

import com.essence.essenceapp.feature.album.model.AlbumSimple
import com.essence.essenceapp.feature.artist.model.ArtistSimple
import java.time.LocalDate

data class Song(
    val id: Long,
    val title: String,
    val durationMs: Int,
    val hlsMasterKey: String,
    val imageKey: String?,
    val songType: String?,
    val totalPlays: Int?,
    val artists: List<ArtistSimple>,
    val album: AlbumSimple?,
    val releaseDate: LocalDate?
)
