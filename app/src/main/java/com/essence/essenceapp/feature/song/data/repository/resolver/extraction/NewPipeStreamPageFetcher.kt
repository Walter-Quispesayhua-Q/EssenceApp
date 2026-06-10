package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

import android.util.Log
import com.essence.essenceapp.core.extractor.youtube.protocol.YoutubeProtocolInitializer
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.stream.StreamExtractor
import java.io.IOException

/**
 * Carga la pagina de YouTube Music para una cancion usando NewPipe.
 *
 * Antes de pedir la pagina, asegura que el protocolo de YouTube este preparado:
 * NewPipe inicializado, policy aplicada y provider de poToken registrado cuando
 * corresponda. Devuelve un StreamExtractor hidratado para que el reader lo
 * convierta en datos crudos de cancion.
 *
 * Solo reintenta fallos de red. Captcha y errores de extraccion se propagan
 * porque suelen indicar bloqueo, contenido no disponible o parseo roto.
 */
@Singleton
class NewPipeStreamPageFetcher @Inject constructor(
    private val youtubeProtocolInitializer: YoutubeProtocolInitializer
) {

    suspend fun fetch(hlsMasterKey: String): StreamExtractor = withContext(Dispatchers.IO) {
        youtubeProtocolInitializer.ensureInitialized()

        withRetry(hlsMasterKey) {
            val url = "$YOUTUBE_MUSIC_BASE$hlsMasterKey"
            val extractor = ServiceList.YouTube.getStreamExtractor(url)
            extractor.fetchPage()
            extractor
        }
    }

    private suspend fun <T> withRetry(
        hlsMasterKey: String,
        block: () -> T
    ): T {
        var lastError: Throwable? = null
        var delayMs = INITIAL_BACKOFF_MS
        var attempt = 0

        while (attempt <= MAX_RETRIES) {
            try {
                if (attempt > 0) {
                    Log.d(TAG, "fetch[$hlsMasterKey] retry attempt=$attempt/$MAX_RETRIES")
                }
                return block()
            } catch (e: ReCaptchaException) {
                Log.w(TAG, "fetch[$hlsMasterKey] reCaptcha challenge, not retrying")
                throw e
            } catch (e: ExtractionException) {
                Log.w(
                    TAG,
                    "fetch[$hlsMasterKey] extraction failed, not retrying: " +
                            "${e.javaClass.simpleName} ${e.message}"
                )
                throw e
            } catch (e: IOException) {
                lastError = e
                Log.w(
                    TAG,
                    "fetch[$hlsMasterKey] attempt=$attempt failed: " +
                            "${e.javaClass.simpleName} ${e.message}"
                )
            }

            attempt++
            if (attempt <= MAX_RETRIES) {
                delay(delayMs)
                delayMs *= 2
            }
        }

        Log.e(TAG, "fetch[$hlsMasterKey] giving up after ${MAX_RETRIES + 1} attempts")
        throw lastError ?: IllegalStateException("retry without error")
    }

    private companion object {
        const val TAG = "NewPipeStreamPageFetcher"
        const val YOUTUBE_MUSIC_BASE = "https://music.youtube.com/watch?v="

        // Hasta 3 intentos en total: 1 original + 2 retries.
        private const val MAX_RETRIES = 2
        private const val INITIAL_BACKOFF_MS = 250L
    }
}