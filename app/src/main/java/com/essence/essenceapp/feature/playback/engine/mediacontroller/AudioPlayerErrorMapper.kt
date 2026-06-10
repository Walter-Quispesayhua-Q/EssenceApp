package com.essence.essenceapp.feature.playback.engine.mediacontroller

import androidx.media3.common.PlaybackException
import androidx.media3.datasource.HttpDataSource
import com.essence.essenceapp.feature.playback.engine.AudioPlayerError
import javax.inject.Inject

/**
 * Traduce errores tecnicos de Media3 a errores simples del engine.
 *
 * Asi el resto de playback no necesita conocer codigos HTTP, codigos internos
 * de ExoPlayer ni detalles de la fuente que fallo.
 */
class AudioPlayerErrorMapper @Inject constructor() {

    fun map(error: PlaybackException): AudioPlayerError {
        if (error.isExpiredSourceError()) {
            return AudioPlayerError.SourceExpired(
                message = "La URL del audio expiro."
            )
        }

        if (error.isUnauthorizedError()) {
            return AudioPlayerError.Unauthorized(
                message = "Tu sesion expiro."
            )
        }

        return when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
            PlaybackException.ERROR_CODE_IO_UNSPECIFIED -> {
                AudioPlayerError.Network(error.message ?: "Error de red.")
            }

            PlaybackException.ERROR_CODE_DECODING_FAILED,
            PlaybackException.ERROR_CODE_DECODING_RESOURCES_RECLAIMED,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES -> {
                AudioPlayerError.Decoder(
                    error.message ?: "No se pudo decodificar el audio."
                )
            }

            else -> {
                AudioPlayerError.Unknown(
                    message = error.message ?: error.errorCodeName,
                    cause = error
                )
            }
        }
    }

    private fun PlaybackException.isExpiredSourceError(): Boolean {
        val httpError = httpErrorOrNull() ?: return false
        return httpError.responseCode == 403 || httpError.responseCode == 410
    }

    private fun PlaybackException.isUnauthorizedError(): Boolean {
        val httpError = httpErrorOrNull() ?: return false
        return httpError.responseCode == 401
    }

    private fun PlaybackException.httpErrorOrNull(): HttpDataSource.InvalidResponseCodeException? {
        if (errorCode != PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) return null
        return cause as? HttpDataSource.InvalidResponseCodeException
    }
}
