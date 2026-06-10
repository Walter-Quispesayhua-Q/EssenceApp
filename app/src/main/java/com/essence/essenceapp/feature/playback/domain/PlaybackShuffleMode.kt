package com.essence.essenceapp.feature.playback.domain

/**
 * Modo aleatorio de la cola actual.
 *
 * Indica si la reproducción debe seguir el orden original de la cola o elegir
 * las próximas canciones usando un orden mezclado.
 */
enum class PlaybackShuffleMode {
    OFF,
    ON
}