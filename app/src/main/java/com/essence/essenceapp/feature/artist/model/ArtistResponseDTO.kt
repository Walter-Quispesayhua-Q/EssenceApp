package com.essence.essenceapp.feature.artist.model

import com.essence.essenceapp.feature.song.model.SongResponseSimpleDTO

data class ArtistResponseDTO(
    val id: Long,
    val nameArtist: String,
    val description: String,
    val imageKey: String,
    val artistUrl: String,
    val country: String,

    val songs: List<SongResponseSimpleDTO>,
    val albums: List<ArtistResponseSimpleDTO>
)
