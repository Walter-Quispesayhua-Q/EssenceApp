package com.essence.essenceapp.feature.history.data.mapper

import com.essence.essenceapp.feature.history.data.dto.HistoryApiDTO
import com.essence.essenceapp.feature.history.domain.model.History

fun History.historyToDto(): HistoryApiDTO {
    return HistoryApiDTO(
        playlistId = this.playlistId,
        albumId = this.albumId,
        durationListenedMs = this.durationListenedMs,
        completed = this.completed,
        skipped = this.skipped,
        skipPositionMs = this.skipPositionMs,
        deviceType = this.deviceType
    )
}