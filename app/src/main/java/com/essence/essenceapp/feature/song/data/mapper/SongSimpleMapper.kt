package com.essence.essenceapp.feature.song.data.mapper

import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import com.essence.essenceapp.feature.song.domain.model.SongSimple

fun SongResponseSimpleApiDTO.songToSimpleDomain(): SongSimple? {
    return SongSimple(
        id = this.id,
        title = this.title ?: return null,
        durationMs = this.durationMs ?: return null,
        hlsMasterKey = this.hlsMasterKey?.takeIf { it.isNotBlank() } ?: return null,
        imageKey = this.imageKey,
        songType = this.songType,
        totalPlays = this.totalPlays,
        artistName = this.artistName?.takeIf { it.isNotBlank() } ?: "Artista desconocido",
        albumName = this.albumName,
        releaseDate = this.releaseDate
    )
}