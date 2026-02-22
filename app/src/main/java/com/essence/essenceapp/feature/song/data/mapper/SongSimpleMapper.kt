package com.essence.essenceapp.feature.song.data.mapper

import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import com.essence.essenceapp.feature.song.domain.model.SongSimple

fun SongResponseSimpleApiDTO.songToSimpleDomain(): SongSimple? {
    return SongSimple(
        id = this.id ?: return null,
        durationMs = this.durationMs ?: return null,
        hlsMasterKey = this.hlsMasterKey ?: return null,
        imageKey = this.imageKey,
        songType = this.songType,
        totalPlays = this.totalPlays,
        artistName = this.artistName ?: return null,
        albumName = this.albumName,
        releaseDate = this.releaseDate
    )
}