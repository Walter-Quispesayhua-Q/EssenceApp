package com.essence.essenceapp.feature.song.data.mapper

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import com.essence.essenceapp.feature.artist.data.mapper.artistToSimpleDomain
import com.essence.essenceapp.feature.album.data.mapper.albumToSimpleDomain
import com.essence.essenceapp.feature.song.domain.model.Song

fun SongResponseApiDTO.songToDomain(): Song? {
    return Song(
        id = this.id ?: return null,
        title = this.title ?: return null,
        durationMs = this.durationMs ?: return null,
        hlsMasterKey = this.hlsMasterKey ?: return null,
        imageKey = this.imageKey,
        songType = this.songType,
        totalPlays = this.totalPlays,
        artists = this.artists?.mapNotNull
        { it.artistToSimpleDomain() } ?: emptyList(),
        album = this.album?.albumToSimpleDomain(),
        releaseDate = this.releaseDate
    )
}