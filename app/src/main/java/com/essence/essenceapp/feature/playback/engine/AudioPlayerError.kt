package com.essence.essenceapp.feature.playback.engine

/**
 * Error técnico producido por el motor de audio.
 *
 * Mantiene separados los errores del reproductor frente a los errores de
 * dominio. El controller será quien traduzca esto a PlaybackError.
 */
sealed interface AudioPlayerError {
    val message: String

    data class Network(
        override val message: String
    ) : AudioPlayerError

    data class SourceExpired(
        override val message: String
    ) : AudioPlayerError

    data class Unauthorized(
        override val message: String
    ) : AudioPlayerError

    data class Decoder(
        override val message: String
    ) : AudioPlayerError

    data class Unknown(
        override val message: String,
        val cause: Throwable? = null
    ) : AudioPlayerError
}