package com.essence.essenceapp.feature.playlist.data.mapper

import com.essence.essenceapp.feature.playlist.data.dto.PlaylistResponseApiDTO
import com.essence.essenceapp.feature.playlist.domain.model.Playlist

fun PlaylistResponseApiDTO.playlistToDomain(): Playlist? {
    return Playlist(
        id = this.id ?: return null,
        title = this.title ?: return null,
        description = this.description,
        imageKey = this.imageKey,
        isPublic = this.isPublic ?: return null,
        totalSongs = this.totalSongs ?: return null,
        createdAt = this.createdAt ?: return null,
        updatedAt = this.updatedAt,
        totalLikes = this.totalLikes,
        isLiked = this.isLiked ?: false
    )
}