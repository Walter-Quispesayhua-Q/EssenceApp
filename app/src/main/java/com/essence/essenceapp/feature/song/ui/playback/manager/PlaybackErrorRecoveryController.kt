package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_RECOVERY"
private const val ERROR_RECOVERY_INITIAL_DELAY_MS = 4_000L
private const val ERROR_RECOVERY_BETWEEN_ATTEMPTS_MS = 5_000L
private const val MAX_ERROR_RECOVERY_ATTEMPTS = 3
private const val ERROR_RECOVERY_RESET_PLAYBACK_MS = 8_000L

@Singleton
class PlaybackErrorRecoveryController @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var job: Job? = null
    private var attempts: Int = 0
    private var lastStablePlaybackStartMs: Long = 0L

    fun scheduleStep(
        isErrorStillPresent: () -> Boolean,
        canGoNext: () -> Boolean,
        onRetry: () -> Unit,
        onSkipToNext: () -> Unit
    ) {
        if (job?.isActive == true) return

        job = scope.launch {
            try {
                val waitMs = if (attempts == 0) {
                    ERROR_RECOVERY_INITIAL_DELAY_MS
                } else {
                    ERROR_RECOVERY_BETWEEN_ATTEMPTS_MS
                }
                delay(waitMs)

                if (!isErrorStillPresent()) return@launch

                if (attempts >= MAX_ERROR_RECOVERY_ATTEMPTS) {
                    if (canGoNext()) {
                        Log.d(
                            TAG,
                            "Agotados $MAX_ERROR_RECOVERY_ATTEMPTS intentos sin recuperacion, saltando a la siguiente cancion"
                        )
                        attempts = 0
                        lastStablePlaybackStartMs = 0L
                        onSkipToNext()
                    }
                    return@launch
                }

                attempts++
                Log.d(TAG, "Intento de recuperacion $attempts/$MAX_ERROR_RECOVERY_ATTEMPTS")
                lastStablePlaybackStartMs = 0L
                onRetry()
            } finally {
                job = null
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }

    fun noteStablePlayback() {
        if (lastStablePlaybackStartMs == 0L) {
            lastStablePlaybackStartMs = System.currentTimeMillis()
        } else if (
            attempts > 0 &&
            System.currentTimeMillis() - lastStablePlaybackStartMs >= ERROR_RECOVERY_RESET_PLAYBACK_MS
        ) {
            Log.d(TAG, "Reproduccion estable, reseteando contador de recuperacion")
            attempts = 0
        }
    }

    fun noteNoPlayback() {
        lastStablePlaybackStartMs = 0L
    }

    fun reset() {
        cancel()
        attempts = 0
        lastStablePlaybackStartMs = 0L
    }
}
