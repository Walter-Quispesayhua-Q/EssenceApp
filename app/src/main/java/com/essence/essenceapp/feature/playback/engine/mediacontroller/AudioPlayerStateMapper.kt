package com.essence.essenceapp.feature.playback.engine.mediacontroller

import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.essence.essenceapp.feature.playback.engine.AudioPlayRequest
import com.essence.essenceapp.feature.playback.engine.AudioPlayerState
import javax.inject.Inject

/**
 * Lee el estado actual de MediaController y lo convierte a AudioPlayerState.
 *
 * El engine publica este estado simple para que el controller de playback no
 * tenga que leer directamente propiedades de Media3.
 */
class AudioPlayerStateMapper @Inject constructor() {

    fun map(
        controller: MediaController,
        currentRequest: AudioPlayRequest?,
        previous: AudioPlayerState
    ): AudioPlayerState {
        return previous.copy(
            mediaId = currentRequest?.mediaId
                ?: controller.currentMediaItem?.mediaId?.takeIf { it.isNotBlank() },
            isPlaying = controller.isPlaying,
            isBuffering = controller.playbackState == Player.STATE_BUFFERING,
            positionMs = controller.currentPosition.toSafeTime(),
            durationMs = controller.duration.toSafeTime(),
            bufferedMs = controller.bufferedPosition.toSafeTime(),
            hasEnded = controller.playbackState == Player.STATE_ENDED
        )
    }

    private fun Long.toSafeTime(): Long =
        if (this == C.TIME_UNSET || this < 0L) 0L else this
}