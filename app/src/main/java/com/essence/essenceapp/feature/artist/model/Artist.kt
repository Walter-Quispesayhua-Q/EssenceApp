package com.essence.essenceapp.feature.artist.model

import com.essence.essenceapp.feature.song.model.SongSimple

data class Artist(
    val id: Long,
    val nameArtist: String,
    val description: String?,
    val imageKey: String?,
    val artistUrl: String,
    val country: String?,
    val songs: List<SongSimple>?,
    val albums: List<ArtistSimple>
)
