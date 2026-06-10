package com.essence.essenceapp.feature.playback.domain

/**
 * Modo de repetición usado por la cola actual.
 *
 * Permite reproducir sin repetir, repetir solo la canción actual o repetir
 * toda la cola cuando llegue al final.
 */
enum class PlaybackRepeatMode {
    OFF,
    ONE,
    ALL
}