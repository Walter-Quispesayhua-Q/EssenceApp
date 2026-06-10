package com.essence.essenceapp.feature.playback.engine.mediacontroller

import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.essence.essenceapp.feature.playback.engine.AudioPlayRequest
import javax.inject.Inject

/**
 * Ejecuta comandos directos sobre MediaController.
 *
 * Aqui viven las operaciones concretas de Media3: cargar media, reproducir,
 * pausar, detener, buscar posicion y cambiar repeat.
 */
class MediaControllerPlaybackCommands @Inject constructor(
    private val mediaItemFactory: AudioMediaItemFactory
) {

    fun play(
        controller: MediaController,
        request: AudioPlayRequest,
        previousRequest: AudioPlayRequest?,
        forceRestart: Boolean,
        autoPlay: Boolean
    ) {
        val sameSource = previousRequest?.url == request.url &&
                controller.currentMediaItem != null

        if (!forceRestart && sameSource) {
            if (request.startPositionMs > 0L) {
                controller.seekTo(request.startPositionMs)
            }

            if (controller.playbackState == Player.STATE_IDLE) {
                controller.prepare()
            }

            if (autoPlay) {
                controller.play()
            } else {
                controller.pause()
            }
            return
        }

        controller.setMediaItem(
            mediaItemFactory.create(request),
            request.startPositionMs.coerceAtLeast(0L)
        )
        controller.prepare()
        if (autoPlay) {
            controller.play()
        } else {
            controller.pause()
        }
    }

    fun resume(controller: MediaController) {
        if (controller.playbackState == Player.STATE_IDLE &&
            controller.currentMediaItem != null
        ) {
            controller.prepare()
        }

        controller.play()
    }

    fun pause(controller: MediaController) {
        controller.pause()
    }

    fun stop(controller: MediaController) {
        controller.stop()
        controller.clearMediaItems()
    }

    fun seekTo(
        controller: MediaController,
        positionMs: Long
    ) {
        controller.seekTo(positionMs.coerceAtLeast(0L))
    }

    fun setRepeatOne(
        controller: MediaController,
        enabled: Boolean
    ) {
        controller.repeatMode = if (enabled) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }
}
