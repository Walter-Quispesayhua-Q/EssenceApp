package com.essence.essenceapp.feature.home.domain.model

import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.song.domain.model.SongSimple

data class Home(
    val songs: List<SongSimple>,
    val albums: List<AlbumSimple>,
    val artists: List<ArtistSimple>,
    val status: HomeStatus
)
