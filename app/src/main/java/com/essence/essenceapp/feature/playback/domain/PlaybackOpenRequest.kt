package com.essence.essenceapp.feature.playback.domain

/**
 * Pedido para iniciar reproducción desde una cola concreta.
 *
 * Define qué cola se va a usar, desde qué canción comenzar, en qué posición
 * iniciar y de dónde viene esa reproducción dentro de la app.
 */
data class PlaybackOpenRequest(
    val items: List<PlaybackQueueItem>,
    val startIndex: Int = 0,
    val startPositionMs: Long = 0L,
    val autoPlay: Boolean = true,
    val source: PlaybackSource
)

fun PlaybackOpenRequest.currentHlsMasterKey(): String? =
    items.getOrNull(startIndex)?.hlsMasterKey