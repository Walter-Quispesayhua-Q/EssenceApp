package com.essence.essenceapp.feature.playback.domain

/**
 * Estado principal en el que se encuentra la reproducción.
 *
 * Resume si el player está vacío, cargando, reproduciendo, pausado, finalizado
 * o detenido por un error. Reemplaza los booleanos sueltos para que el estado
 * sea más claro y difícil de combinar mal.
 */
sealed interface PlaybackState {
    data object Idle : PlaybackState
    data object Buffering : PlaybackState
    data object Playing : PlaybackState
    data object Paused : PlaybackState
    data object Ended : PlaybackState
    data class Failed(val cause: PlaybackError) : PlaybackState
}