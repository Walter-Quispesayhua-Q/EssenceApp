package com.essence.essenceapp.shared.streaming

import android.util.Log
import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.song.data.api.SongApiService
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Singleton
class StreamingUrlSyncManager @Inject constructor(
    private val songApiService: SongApiService,
    @ApplicationScope private val appScope: CoroutineScope
) {

    private val pending = ConcurrentHashMap<String, Job>()

    fun schedule(
        videoId: String,
        freshUrl: String,
        isStillCurrent: () -> Boolean
    ) {
        pending[videoId]?.cancel()
        pending[videoId] = appScope.launch {
            try {
                delay(CONFIRM_DELAY_MS)
                if (!isStillCurrent()) {
                    Log.d(TAG, "Sync descartado (skip <${CONFIRM_DELAY_MS}ms): $videoId")
                    return@launch
                }
                val ok = attemptSync(videoId, freshUrl)
                if (!ok && isStillCurrent()) {
                    delay(RETRY_DELAY_MS)
                    if (isStillCurrent()) attemptSync(videoId, freshUrl)
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.w(TAG, "Sync error inesperado para $videoId: ${e.message}")
            } finally {
                pending.remove(videoId)
            }
        }
    }

    private suspend fun attemptSync(videoId: String, freshUrl: String): Boolean {
        return try {
            val response = songApiService.refreshStreamingUrl(videoId, freshUrl)
            if (response.isSuccessful) {
                Log.d(TAG, "Sync OK: $videoId")
                true
            } else {
                Log.w(TAG, "Sync HTTP ${response.code()}: $videoId")
                response.code() in 500..599
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: HttpException) {
            Log.w(TAG, "Sync HttpException ${e.code()}: $videoId")
            e.code() in 500..599
        } catch (e: Exception) {
            Log.w(TAG, "Sync IOException: ${e.message}")
            true
        }
    }

    companion object {
        private const val TAG = "StreamSync"
        private const val CONFIRM_DELAY_MS = 3_000L
        private const val RETRY_DELAY_MS = 1_500L
    }
}
