package com.essence.essenceapp.feature.song.ui.playback.engine

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import com.essence.essenceapp.core.network.ApiConstants
import com.essence.essenceapp.core.storage.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PROVIDER_USER_AGENT = "EssenceAppPlayer"
private const val CONNECT_TIMEOUT_MS = 25_000
private const val READ_TIMEOUT_MS = 45_000

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

        val resolvingFactory: DataSource.Factory =
            ResolvingDataSource.Factory(httpFactory) { dataSpec ->
                if (!useAuthHeader) {
                    dataSpec
                } else {
                    val token = tokenManager.getCachedToken()
                    if (token.isNullOrBlank()) {
                        dataSpec
                    } else {
                        dataSpec.buildUpon()
                            .setHttpRequestHeaders(
                                dataSpec.httpRequestHeaders + mapOf(
                                    "Authorization" to "Bearer $token"
                                )
                            )
                            .build()
                    }
                }
            }

        val upstreamDataSourceFactory = DefaultDataSource.Factory(context, resolvingFactory)

        return CacheDataSource.Factory()
            .setCache(mediaAudioCache.cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun shouldAttachAuthHeader(url: String): Boolean {
        val apiHost = Uri.parse(ApiConstants.BASE_URL).host ?: return false
        val streamHost = Uri.parse(url).host ?: return false
        return apiHost.equals(streamHost, ignoreCase = true)
    }
}
