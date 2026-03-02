package com.essence.essenceapp.feature.search.data.mapper

import com.essence.essenceapp.feature.album.data.mapper.albumToSimpleDomain
import com.essence.essenceapp.feature.artist.data.mapper.artistToSimpleDomain
import com.essence.essenceapp.feature.search.data.dto.SearchApiDTO
import com.essence.essenceapp.feature.search.domain.model.Search
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain

fun SearchApiDTO.searchToDomain(): Search {
    return Search(
        songs = this.songs?.mapNotNull {
            it.songToSimpleDomain()
        },
        albums = this.albums?.mapNotNull {
            it.albumToSimpleDomain()
        },
        artists = this.artists?.mapNotNull {
            it.artistToSimpleDomain()
        }
    )
}