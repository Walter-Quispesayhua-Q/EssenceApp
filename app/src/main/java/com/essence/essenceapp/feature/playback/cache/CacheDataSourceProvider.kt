package com.essence.essenceapp.feature.playback.cache

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import com.essence.essenceapp.core.network.ApiConstants
import com.essence.essenceapp.core.storage.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PROVIDER_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0"

private const val YOUTUBE_ORIGIN = "https://www.youtube.com"
private const val CONNECT_TIMEOUT_MS = 25_000
private const val READ_TIMEOUT_MS = 45_000

/**
 * Construye el origen de datos usado por Media3 para leer audio.
 *
 * Primero intenta leer desde el cache local y, si faltan bytes, va a red.
 * También agrega el token de autorización solo cuando la URL pertenece al
 * backend propio de la app.
 */
@Singleton
@OptIn(UnstableApi::class)
class CacheDataSourceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager,
    private val mediaAudioCache: MediaAudioCache
) {

    fun createFactory(useAuthHeader: Boolean): DataSource.Factory {
        val httpFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(PROVIDER_USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(CONNECT_TIMEOUT_MS)
            .setReadTimeoutMs(READ_TIMEOUT_MS)

        val resolvingFactory = ResolvingDataSource.Factory(httpFactory) { dataSpec ->
            val youtubeHeaders = dataSpec.uri.host
                ?.takeIf { host ->
                    host.endsWith("googlevideo.com", ignoreCase = true) ||
                            host.endsWith("youtube.com", ignoreCase = true) ||
                            host.endsWith("youtubeusercontent.com", ignoreCase = true)
                }
                ?.let {
                    mapOf(
                        "User-Agent" to PROVIDER_USER_AGENT,
                        "Referer" to "$YOUTUBE_ORIGIN/",
                        "Origin" to YOUTUBE_ORIGIN,
                        "Accept" to "*/*",
                        "Range" to dataSpec.asRangeHeader()
                    )
                }
                .orEmpty()

            val authHeaders = if (useAuthHeader) {
                val token = tokenManager.getCachedToken()
                if (token.isNullOrBlank()) {
                    emptyMap()
                } else {
                    mapOf("Authorization" to "Bearer $token")
                }
            } else {
                emptyMap()
            }

            val mergedHeaders = dataSpec.httpRequestHeaders + youtubeHeaders + authHeaders

            if (mergedHeaders.isEmpty()) {
                dataSpec
            } else {
                dataSpec.buildUpon()
                    .setHttpRequestHeaders(mergedHeaders)
                    .build()
            }
        }

        val upstreamFactory = DefaultDataSource.Factory(context, resolvingFactory)

        return CacheDataSource.Factory()
            .setCache(mediaAudioCache.cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun shouldAttachAuthHeader(url: String): Boolean {
        val apiHost = Uri.parse(ApiConstants.BASE_URL).host ?: return false
        val streamHost = Uri.parse(url).host ?: return false

        return apiHost.equals(streamHost, ignoreCase = true)
    }

    private fun DataSpec.asRangeHeader(): String {
        val rangeStart = position.coerceAtLeast(0L)
        if (length == C.LENGTH_UNSET.toLong()) {
            return "bytes=$rangeStart-"
        }

        val rangeEnd = rangeStart + length - 1L
        return "bytes=$rangeStart-$rangeEnd"
    }
}
