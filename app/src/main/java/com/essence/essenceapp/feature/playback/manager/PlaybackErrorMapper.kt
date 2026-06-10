package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.feature.playback.domain.PlaybackError
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.engine.AudioPlayerError
import javax.inject.Inject

/**
 * Traduce errores tecnicos del engine a errores publicos de playback.
 *
 * El engine habla de red, decoder o URL vencida. Playback convierte eso en
 * errores que la UI puede mostrar y que el manager puede usar para decidir.
 */
class PlaybackErrorMapper @Inject constructor() {

    fun map(
        error: AudioPlayerError,
        item: PlaybackQueueItem?
    ): PlaybackError =
        when (error) {
            is AudioPlayerError.Network -> PlaybackError.Network(error.message)

            is AudioPlayerError.SourceExpired -> PlaybackError.UrlExpired(
                songId = item?.songId,
                hlsMasterKey = item?.hlsMasterKey.orEmpty(),
                message = error.message
            )

            is AudioPlayerError.Unauthorized -> PlaybackError.EngineFailed(error.message)

            is AudioPlayerError.Decoder -> PlaybackError.EngineFailed(error.message)

            is AudioPlayerError.Unknown -> PlaybackError.Unknown(
                message = error.message,
                cause = error.cause
            )
        }
}