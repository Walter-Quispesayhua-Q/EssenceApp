package com.essence.essenceapp.feature.home.data.mapper

import com.essence.essenceapp.feature.home.data.dto.HomeStatusApiDTO
import com.essence.essenceapp.feature.home.domain.model.HomeStatus

fun HomeStatusApiDTO.homeStatusToDomain(): HomeStatus? {
    return HomeStatus(
        songsLoaded = this.songsLoaded ?: return null,
        albumsLoaded = this.albumsLoaded ?: return null,
        artistsLoaded = this.artistsLoaded ?: return null,
        error = this.error
    )
}