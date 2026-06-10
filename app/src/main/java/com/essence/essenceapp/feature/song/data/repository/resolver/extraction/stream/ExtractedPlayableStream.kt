package com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream

import java.time.Instant

/**
 * Stream reproducible elegido desde NewPipe.
 *
 * Es un modelo tecnico e interno de la extraccion: guarda la URL final y los
 * datos utiles para logs, cache y expiracion, sin conocer el modelo Song.
 */
data class ExtractedPlayableStream(
    val kind: ExtractedStreamKind,
    val url: String,
    val expiresAt: Instant?,
    val itag: Int? = null,
    val bitrate: Int? = null,
    val mimeType: String? = null
)