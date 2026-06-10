package com.essence.essenceapp.feature.playback.engine

/**
 * Estado técnico emitido por el motor de audio.
 *
 * Describe lo que está pasando dentro del reproductor: si está cargando,
 * sonando, pausado, finalizado, cuánto avanzó y si necesita que playback
 * consiga una nueva URL para continuar.
 */
data class AudioPlayerState(
    val mediaId: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedMs: Long = 0L,
    val hasEnded: Boolean = false,
    val requiresSourceRefresh: Boolean = false,
    val error: AudioPlayerError? = null
)