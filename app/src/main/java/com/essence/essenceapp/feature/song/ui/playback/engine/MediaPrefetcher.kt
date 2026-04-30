package com.essence.essenceapp.feature.song.ui.playback.engine

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "MEDIA_PREFETCH"

@Singleton
@OptIn(UnstableApi::class)
class MediaPrefetcher @Inject constructor(
    private val cacheDataSourceProvider: CacheDataSourceProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var currentUrl: String? = null

    @Volatile
    private var currentWriter: CacheWriter? = null

    private var currentJob: Job? = null

    fun prefetch(url: String) {
        if (url.isBlank()) return
        if (currentUrl == url && currentJob?.isActive == true) return

        cancel()

        val targetUrl = url
        currentUrl = targetUrl

        currentJob = scope.launch {
            try {
                Log.d(TAG, "Iniciando prefetch: $targetUrl")
                val useAuthHeader = cacheDataSourceProvider.shouldAttachAuthHeader(targetUrl)
                val factory = cacheDataSourceProvider.createFactory(useAuthHeader)
                val cacheDataSource = factory.createDataSource() as CacheDataSource
                val dataSpec = DataSpec.Builder()
                    .setUri(Uri.parse(targetUrl))
                    .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                    .build()

                val writer = CacheWriter(
                    cacheDataSource,
                    dataSpec,
                    null,
                    null
                )
                currentWriter = writer
                writer.cache()
                Log.d(TAG, "Prefetch completado: $targetUrl")
            } catch (ce: CancellationException) {
                Log.d(TAG, "Prefetch cancelado: $targetUrl")
                throw ce
            } catch (e: Exception) {
                Log.w(TAG, "Prefetch fallo (no critico) $targetUrl: ${e.message}")
            } finally {
                if (currentUrl == targetUrl) {
                    currentUrl = null
                    currentWriter = null
                }
            }
        }
    }

    fun cancel() {
        currentWriter?.cancel()
        currentWriter = null
        currentJob?.cancel()
        currentJob = null
        currentUrl = null
    }
}
