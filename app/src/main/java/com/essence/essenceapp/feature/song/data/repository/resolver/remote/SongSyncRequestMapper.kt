package com.essence.essenceapp.feature.song.data.repository.resolver.remote

import com.essence.essenceapp.feature.song.data.dto.SongSyncRequestApiDTO
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractedSongData

/**
 * Convierte los datos crudos del extractor en el cuerpo del POST /sync.
 *
 * Es un mapeo 1:1 entre los campos de ExtractedSongData y los del DTO que
 * espera la api. No transforma valores ni decide si los datos son
 * suficientes para sincronizar; la decision la toma SongSyncSender.
 */
fun ExtractedSongData.toApiSyncRequest(): SongSyncRequestApiDTO = SongSyncRequestApiDTO(
    hlsMasterKey = hlsMasterKey,
    title = title,
    durationMs = durationMs,
    uploaderName = uploaderName,
    uploaderUrl = uploaderUrl,
    thumbnailUrl = thumbnailUrl,
    streamingUrl = streamingUrl,
    streamingUrlExpiresAt = streamingUrlExpiresAt,
    viewCount = viewCount,
    releaseDate = releaseDate
)