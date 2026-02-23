package com.essence.essenceapp.feature.home.data.dto

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseSimpleApiDTO
import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseSimpleApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO

data class HomeResponseApiDTO(
    val songs: List<SongResponseSimpleApiDTO>?,
    val albums: List<AlbumResponseSimpleApiDTO>?,
    val artists: List<ArtistResponseSimpleApiDTO>?,
    val status: HomeStatusApiDTO?
)
