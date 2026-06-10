package com.essence.essenceapp.feature.playback.engine.mediacontroller

import androidx.media3.session.MediaController
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val POSITION_UPDATE_MS = 500L

/**
 * Mantiene actualizado el progreso mientras el audio esta sonando.
 *
 * Solo emite ticks de posicion. No decide reproduccion, no cambia canciones y
 * no interpreta errores.
 */
class AudioPositionTracker @Inject constructor() {
    private var job: Job? = null
    private var trackedController: MediaController? = null

    fun start(
        scope: CoroutineScope,
        controller: MediaController,
        onTick: (MediaController) -> Unit
    ) {
        if (trackedController === controller && job?.isActive == true) return

        stop()

        trackedController = controller
        job = scope.launch {
            while (isActive && trackedController === controller) {
                onTick(controller)
                delay(POSITION_UPDATE_MS)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        trackedController = null
    }
}