package com.essence.essenceapp.feature.playback.engine.mediacontroller

import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.essence.essenceapp.feature.playback.service.MediaControllerProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mantiene la conexion entre el engine y la MediaSession.
 *
 * Se encarga de obtener el MediaController, registrar el listener del engine y
 * liberar la conexion cuando playback ya no la necesita.
 */
@Singleton
class MediaControllerConnection @Inject constructor(
    private val controllerProvider: MediaControllerProvider
) {
    private var attachedController: MediaController? = null

    suspend fun get(listener: Player.Listener): MediaController {
        val controller = controllerProvider.get()

        if (attachedController !== controller) {
            attachedController?.removeListener(listener)
            attachedController = controller
            controller.addListener(listener)
        }

        return controller
    }

    fun current(): MediaController? =
        attachedController

    fun release(listener: Player.Listener) {
        attachedController?.removeListener(listener)
        attachedController = null
        controllerProvider.release()
    }

    suspend fun warmUp() {
        controllerProvider.warmUp()
    }
}