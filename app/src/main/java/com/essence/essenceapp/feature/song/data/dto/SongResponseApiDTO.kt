package com.essence.essenceapp.feature.song.data.dto

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseSimpleApiDTO
import com.essence.essenceapp.feature.album.model.AlbumSimple
import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseSimpleApiDTO
import com.essence.essenceapp.feature.artist.model.ArtistSimple
import java.time.LocalDate

data class SongResponseApiDTO(
    val id: Long?,
    val title: String?,
    val durationMs: Int?,
    val hlsMasterKey: String?,
    val imageKey: String?,
    val songType: String?,
    val totalPlays: Int?,
    val artists: List<ArtistResponseSimpleApiDTO>?,
    val album: AlbumResponseSimpleApiDTO?,
    val releaseDate: LocalDate?
)
