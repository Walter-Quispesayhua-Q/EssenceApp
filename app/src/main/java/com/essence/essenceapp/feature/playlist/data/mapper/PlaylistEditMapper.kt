package com.essence.essenceapp.feature.playlist.data.mapper

import com.essence.essenceapp.feature.playlist.data.dto.PlaylistEditResponseApiDTO
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistEditable

fun PlaylistEditResponseApiDTO.toEditableDomain(): PlaylistEditable? {
    return PlaylistEditable(
        id = this.id ?: return null,
        title = this.title ?: return null,
        description = this.description,
        imageKey = this.imageKey,
        isPublic = this.isPublic ?: return null,
        type = this.type ?: "NORMAL"
    )
}
