package com.essence.essenceapp.feature.artist.domain.model

import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.song.domain.model.SongSimple

data class Artist(
    val id: Long,
    val nameArtist: String,
    val description: String?,
    val imageKey: String?,
    val artistUrl: String,
    val country: String?,
    val songs: List<SongSimple>?,
    val albums: List<AlbumSimple>?,
    val isLiked: Boolean
)
