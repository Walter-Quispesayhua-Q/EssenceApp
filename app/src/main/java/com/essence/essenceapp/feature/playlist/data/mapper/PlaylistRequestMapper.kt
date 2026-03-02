package com.essence.essenceapp.feature.playlist.data.mapper

import com.essence.essenceapp.feature.playlist.data.dto.PlaylistRequestApiDTO
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest


fun PlaylistRequest.playlistRequestToData(): PlaylistRequestApiDTO {
    return PlaylistRequestApiDTO(
        title = this.title,
        description = this.description,
        isPublic = this.isPublic
    )
}