package com.essence.essenceapp.feature.playback.engine

/**
 * Datos necesarios para que el motor cargue una canción reproducible.
 *
 * La URL llega ya resuelta desde el flujo de song/playback. El engine solo la
 * usa para reproducir audio y enviar metadatos al sistema.
 */
data class AudioPlayRequest(
    val url: String,
    val mediaId: String,
    val title: String,
    val artistName: String,
    val artworkUri: String? = null,
    val startPositionMs: Long = 0L
)