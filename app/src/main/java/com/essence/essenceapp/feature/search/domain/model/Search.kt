package com.essence.essenceapp.feature.search.domain.model

import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.song.domain.model.SongSimple


data class Search(
    val songs: List<SongSimple>?,
    val albums: List<AlbumSimple>?,
    val artists: List<ArtistSimple>?
)