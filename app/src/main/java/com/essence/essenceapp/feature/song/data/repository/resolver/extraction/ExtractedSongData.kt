package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

import java.time.Instant
import java.time.LocalDate

/**
 * Datos crudos que NewPipe expone para una cancion.
 *
 * Es la salida del extractor: titulo, uploader, miniatura, URL de audio
 * con su tiempo de expiracion y metadatos basicos. No conoce el id interno
 * de la app; ese aparece despues, cuando la api persiste la cancion.
 */
data class ExtractedSongData(
    val hlsMasterKey: String,
    val title: String,
    val durationMs: Int,
    val uploaderName: String,
    val uploaderUrl: String,
    val thumbnailUrl: String?,
    val streamingUrl: String?,
    val streamingUrlExpiresAt: Instant?,
    val viewCount: Long,
    val releaseDate: LocalDate?
)
