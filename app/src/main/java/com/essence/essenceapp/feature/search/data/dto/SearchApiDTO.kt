package com.essence.essenceapp.feature.search.data.dto

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseSimpleApiDTO
import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseSimpleApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import kotlinx.serialization.Serializable

@Serializable
data class SearchApiDTO(
    val songs: List<SongResponseSimpleApiDTO>?,
    val albums: List<AlbumResponseSimpleApiDTO>?,
    val artists: List<ArtistResponseSimpleApiDTO>?,
    val hasNextPage: Boolean = false
)