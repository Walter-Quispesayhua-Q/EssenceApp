package com.essence.essenceapp.feature.song.data.repository.resolver.common

import android.util.Log
import com.essence.essenceapp.core.network.qualifier.NewPipeOkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Verifica si una URL de streaming responde antes de entregarla al playback.
 *
 * No reproduce audio ni interpreta formatos: solo hace una lectura minima con
 * Range para detectar URLs que YouTube ya rechaza con 403, 404, 410 o 429.
 */
@Singleton
class StreamingUrlReachabilityValidator @Inject constructor(
    @NewPipeOkHttpClient okHttpClient: OkHttpClient
) {
    private val validationClient = okHttpClient.newBuilder()
        .callTimeout(VALIDATION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .build()

    suspend fun validate(url: String?): StreamingUrlReachability =
        withContext(Dispatchers.IO) {
            val cleanUrl = url?.trim()?.takeIf { it.isNotBlank() }
                ?: return@withContext StreamingUrlReachability.Invalid("blank url")

            val httpUrl = cleanUrl.toHttpUrlOrNull()
                ?: return@withContext StreamingUrlReachability.Invalid("invalid url")

            val request = Request.Builder()
                .url(httpUrl)
                .get()
                .header("Range", VALIDATION_RANGE_HEADER)
                .header("Accept", "*/*")
                .applyYoutubeHeadersIfNeeded(httpUrl.host)
                .build()

            try {
                validationClient.newCall(request).execute().use { response ->
                    when (response.code) {
                        200, 206 -> {
                            Log.d(TAG, "reachable host=${httpUrl.host} code=${response.code}")
                            StreamingUrlReachability.Reachable(response.code)
                        }
                        else -> {
                            Log.w(TAG, "rejected host=${httpUrl.host} code=${response.code}")
                            StreamingUrlReachability.Rejected(response.code)
                        }
                    }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (error: Throwable) {
                Log.w(
                    TAG,
                    "failed host=${httpUrl.host}: " +
                            "${error.javaClass.simpleName} ${error.message}"
                )
                StreamingUrlReachability.Failed(error)
            }
        }

    suspend fun isReachable(url: String?): Boolean {
        return validate(url).isReachable
    }

    private fun Request.Builder.applyYoutubeHeadersIfNeeded(
        host: String
    ): Request.Builder {
        if (!host.isYoutubeStreamHost()) return this

        return header("User-Agent", YOUTUBE_USER_AGENT)
            .header("Referer", "$YOUTUBE_ORIGIN/")
            .header("Origin", YOUTUBE_ORIGIN)
    }

    private fun String.isYoutubeStreamHost(): Boolean {
        return endsWith("googlevideo.com", ignoreCase = true) ||
                endsWith("youtube.com", ignoreCase = true) ||
                endsWith("youtubeusercontent.com", ignoreCase = true)
    }

    private companion object {
        const val TAG = "StreamReachability"

        const val VALIDATION_TIMEOUT_MS = 4_000L
        const val VALIDATION_RANGE_HEADER = "bytes=0-"

        const val YOUTUBE_ORIGIN = "https://www.youtube.com"
        const val YOUTUBE_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) " +
                    "Gecko/20100101 Firefox/140.0"
    }
}
