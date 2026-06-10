package com.essence.essenceapp.feature.playback.domain

/**
 * Errores conocidos que pueden ocurrir durante la reproducción.
 *
 * Permite distinguir si el problema vino de la red, de la resolución de una
 * canción, de una URL vencida, del motor de audio o de una cola inválida.
 */
sealed interface PlaybackError {
    val message: String

    data class Network(
        override val message: String
    ) : PlaybackError

    data class UrlExpired(
        val songId: Long?,
        val hlsMasterKey: String,
        override val message: String
    ) : PlaybackError

    data class ResolveFailed(
        val hlsMasterKey: String,
        val songId: Long?,
        override val message: String
    ) : PlaybackError

    data class EngineFailed(
        override val message: String,
        val cause: Throwable? = null
    ) : PlaybackError

    data object QueueEmpty : PlaybackError {
        override val message: String = "Queue is empty"
    }

    data class Unknown(
        override val message: String,
        val cause: Throwable? = null
    ) : PlaybackError
}