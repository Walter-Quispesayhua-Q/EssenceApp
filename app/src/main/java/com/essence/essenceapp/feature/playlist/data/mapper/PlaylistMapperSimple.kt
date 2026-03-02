package com.essence.essenceapp.feature.playlist.data.mapper

import com.essence.essenceapp.feature.playlist.data.dto.PlaylistResponseSimpleApiDTO
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple

fun PlaylistResponseSimpleApiDTO.playlistToSimpleDomain(): PlaylistSimple? {
    return PlaylistSimple(
        id = this.id ?: return null,
        title = this.title ?: return null,
        isPublic = this.isPublic ?: return null,
        totalLikes = this.totalLikes
    )
}