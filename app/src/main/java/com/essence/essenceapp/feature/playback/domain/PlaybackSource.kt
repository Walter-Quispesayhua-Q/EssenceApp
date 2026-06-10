package com.essence.essenceapp.feature.playback.domain

/**
 * Origen desde donde se inició una reproducción.
 *
 * Permite saber si la música viene de un álbum, playlist, búsqueda, historial
 * u otra sección de la app. Esta información ayuda a registrar historial y
 * entender el contexto de reproducción.
 */
data class PlaybackSource(
    val type: SourceType,
    val sourceId: String? = null
) {
    enum class SourceType {
        ALBUM,
        PLAYLIST,
        ARTIST,
        SEARCH,
        HISTORY,
        SINGLE,
        RADIO
    }
}