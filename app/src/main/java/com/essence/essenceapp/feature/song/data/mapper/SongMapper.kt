package com.essence.essenceapp.feature.song.data.mapper

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import com.essence.essenceapp.feature.song.model.Song

fun SongResponseApiDTO.toDomain(): Song? {
    return Song(
        id = this.id ?: return null,
        title = this.title ?: return null,
        durationMs = this.durationMs ?: return null,
        hlsMasterKey = this.hlsMasterKey ?: return null,
        imageKey = this.imageKey,
        songType = this.songType,
        totalPlays = this.totalPlays,

        releaseDate = this.releaseDate
    )
}