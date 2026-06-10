package com.essence.essenceapp.feature.playback.service

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Crea y reutiliza el MediaController conectado al servicio de reproducción.
 *
 * Permite que otras piezas controlen la MediaSession sin depender directamente
 * de la instancia Android del servicio.
 */
@Singleton
class MediaControllerProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mutex = Mutex()

    private var controller: MediaController? = null

    suspend fun get(): MediaController =
        mutex.withLock {
            val existing = controller
            if (existing != null) return existing

            val token = SessionToken(
                context,
                ComponentName(context, MediaPlaybackService::class.java)
            )

            buildController(token).also { created ->
                controller = created
            }
        }

    fun current(): MediaController? =
        controller

    fun release() {
        controller?.release()
        controller = null
    }

    suspend fun warmUp() {
        get()
    }

    private suspend fun buildController(
        token: SessionToken
    ): MediaController =
        suspendCancellableCoroutine { continuation ->
            val future = MediaController.Builder(context, token).buildAsync()

            continuation.invokeOnCancellation {
                future.cancel(true)
            }

            future.addListener(
                {
                    resumeControllerFuture(
                        future = future,
                        continuation = continuation
                    )
                },
                MoreExecutors.directExecutor()
            )
        }

    private fun resumeControllerFuture(
        future: com.google.common.util.concurrent.ListenableFuture<MediaController>,
        continuation: CancellableContinuation<MediaController>
    ) {
        try {
            continuation.resume(future.get())
        } catch (error: Exception) {
            continuation.resumeWithException(error)
        }
    }
}