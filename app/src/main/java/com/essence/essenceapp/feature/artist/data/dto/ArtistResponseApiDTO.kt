package com.essence.essenceapp.feature.artist.data.dto

import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO

data class ArtistResponseApiDTO(
    val id: Long?,
    val nameArtist: String?,
    val description: String?,
    val imageKey: String?,
    val artistUrl: String?,
    val country: String?,

    val songs: List<SongResponseSimpleApiDTO>?,
    val albums: List<ArtistResponseSimpleApiDTO>?
)
