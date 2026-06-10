package com.essence.essenceapp.feature.playback.domain

/**
 * Progreso temporal de la canción actual.
 *
 * Mantiene la posición reproducida, la duración total y cuánto audio hay
 * cargado en buffer. Va separado del resto del estado porque cambia con mucha
 * frecuencia mientras una canción está sonando.
 */
data class PlaybackPositionInfo(
    val positionMs: Long,
    val durationMs: Long,
    val bufferedMs: Long
) {
    val progress: Float
        get() = if (durationMs > 0L) {
            (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    companion object {
        val Zero = PlaybackPositionInfo(
            positionMs = 0L,
            durationMs = 0L,
            bufferedMs = 0L
        )
    }
}