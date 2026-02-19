package com.essence.essenceapp.feature.album.data.mapper

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseSimpleApiDTO
import com.essence.essenceapp.feature.album.model.AlbumSimple

fun AlbumResponseSimpleApiDTO.albumToSimpleDomain(): AlbumSimple? {
    return AlbumSimple(
        id = this.id ?: return null,
        title = this.title ?: return null,
        imageKey = this.imageKey,
        albumUrl = this.albumUrl ?: return null,
        artists = this.artists,
        release = this.release
    )
}