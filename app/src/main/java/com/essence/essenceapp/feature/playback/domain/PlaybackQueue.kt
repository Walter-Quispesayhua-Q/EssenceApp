package com.essence.essenceapp.feature.playback.domain

/**
 * Cola actual de reproducción.
 *
 * Guarda la lista de canciones preparadas para reproducirse y el índice de la
 * canción activa. Desde aquí se puede consultar la canción actual, las próximas
 * canciones y si es posible avanzar o retroceder.
 */
data class PlaybackQueue(
    val items: List<PlaybackQueueItem>,
    val currentIndex: Int
) {
    val current: PlaybackQueueItem?
        get() = items.getOrNull(currentIndex)

    val hasNext: Boolean
        get() = currentIndex + 1 < items.size

    val hasPrevious: Boolean
        get() = currentIndex > 0

    val upcoming: List<PlaybackQueueItem>
        get() = items.drop(currentIndex + 1)

    fun withIndex(index: Int): PlaybackQueue =
        if (items.isEmpty()) {
            copy(currentIndex = 0)
        } else {
            copy(currentIndex = index.coerceIn(items.indices))
        }
}