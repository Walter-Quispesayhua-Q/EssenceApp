package com.essence.essenceapp.feature.playback.prefetch

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import com.essence.essenceapp.feature.playback.cache.CacheDataSourceProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "MEDIA_PREFETCH"
private const val DEFAULT_PREFETCH_BYTES = 5L * 1024L * 1024L

/**
 * Descarga anticipadamente bytes de audio al cache local.
 *
 * No decide qué canción preparar ni resuelve URLs. Solo recibe una URL fresca,
 * descarga un tramo inicial y lo deja disponible para que Media3 arranque más
 * rápido si esa canción llega a reproducirse.
 */
@Singleton
@OptIn(UnstableApi::class)
class MediaPrefetcher @Inject constructor(
    private val cacheDataSourceProvider: CacheDataSourceProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var currentKey: String? = null

    @Volatile
    private var currentWriter: CacheWriter? = null

    private var currentJob: Job? = null

    fun prefetch(
        key: String,
        url: String,
        maxBytes: Long = DEFAULT_PREFETCH_BYTES
    ) {
        if (key.isBlank() || url.isBlank()) return
        if (currentKey == key && currentJob?.isActive == true) return

        cancel()

        currentKey = key

        currentJob = scope.launch {
            var writer: CacheWriter? = null

            try {
                Log.d(TAG, "Prefetch start key=$key maxBytes=$maxBytes")

                val useAuthHeader = cacheDataSourceProvider.shouldAttachAuthHeader(url)
                val factory = cacheDataSourceProvider.createFactory(useAuthHeader)
                val cacheDataSource = factory.createDataSource() as CacheDataSource

                val dataSpec = DataSpec.Builder()
                    .setUri(Uri.parse(url))
                    .setPosition(0L)
                    .setLength(maxBytes)
                    .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                    .build()

                var lastLoggedBytes = 0L
                var lastLoggedAtMs = 0L
                val progressListener = CacheWriter.ProgressListener {
                    requestLength,
                    bytesCached,
                    newBytesCached ->
                    val nowMs = System.currentTimeMillis()
                    val isComplete = requestLength > 0L && bytesCached >= requestLength
                    val shouldLogByBytes =
                        bytesCached - lastLoggedBytes >= PROGRESS_LOG_STEP_BYTES
                    val shouldLogByTime =
                        nowMs - lastLoggedAtMs >= PROGRESS_LOG_INTERVAL_MS

                    if (isComplete || shouldLogByBytes || shouldLogByTime) {
                        lastLoggedBytes = bytesCached
                        lastLoggedAtMs = nowMs

                        val total = requestLength.takeIf { it > 0L }
                        val percent = total?.let {
                            (bytesCached * 100L / it).coerceAtMost(100L)
                        }
                        Log.d(
                            TAG,
                            "Prefetch progress key=$key cached=$bytesCached/" +
                                "${total ?: "unknown"} bytes " +
                                "percent=${percent ?: "unknown"} new=$newBytesCached"
                        )
                    }
                }

                writer = CacheWriter(
                    cacheDataSource,
                    dataSpec,
                    null,
                    progressListener
                )

                currentWriter = writer
                writer.cache()

                Log.d(TAG, "Prefetch completed key=$key")
            } catch (ce: CancellationException) {
                Log.d(TAG, "Prefetch cancelled key=$key")
                throw ce
            } catch (error: Exception) {
                Log.w(TAG, "Prefetch failed key=$key: ${error.message}")
            } finally {
                if (writer != null && currentWriter === writer) {
                    currentWriter = null
                }

                if (currentKey == key) {
                    currentKey = null
                }
            }
        }
    }

    fun cancel() {
        currentWriter?.cancel()
        currentWriter = null

        currentJob?.cancel()
        currentJob = null

        currentKey = null
    }

    companion object {
        private const val PROGRESS_LOG_STEP_BYTES = 1L * 1024L * 1024L
        private const val PROGRESS_LOG_INTERVAL_MS = 1_000L
    }
}
