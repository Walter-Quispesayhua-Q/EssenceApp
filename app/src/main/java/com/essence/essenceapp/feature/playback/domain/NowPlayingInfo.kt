package com.essence.essenceapp.feature.playback.domain

/**
 * Información visible de la canción que está sonando.
 *
 * Agrupa el item actual con datos de estado que la UI necesita mostrar,
 * como si está marcado con like y si se puede avanzar o retroceder en la cola.
 */
data class NowPlayingInfo(
    val item: PlaybackQueueItem,
    val isLiked: Boolean,
    val canSkipNext: Boolean,
    val canSkipPrevious: Boolean
)