package com.essence.essenceapp.core.extractor.youtube.protocol

import android.util.Log
import com.essence.essenceapp.core.extractor.NewPipeInitializer
import com.essence.essenceapp.core.network.qualifier.NewPipeOkHttpClient
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeStreamExtractor

/**
 * Prepara la integracion de YouTube antes de cualquier extraccion.
 *
 * Aplica la configuracion comun de NewPipe, clientes de YouTube, poToken y
 * warm-up para que los resolvers solo pidan una pagina ya lista para interpretar.
 */
@Singleton
class YoutubeProtocolInitializer @Inject constructor(
    @NewPipeOkHttpClient private val newPipeOkHttpClient: OkHttpClient,
    private val policy: YoutubeClientPolicy) {
    @Volatile
    private var configured = false

    @Volatile
    private var warmUpJob: Job? = null

    fun ensureInitialized() {
        if (!configured) {
            synchronized(this) {
                if (!configured) {
                    NewPipeInitializer.setOkHttpClient(newPipeOkHttpClient)
                    NewPipeInitializer.init()
                    applyYoutubePolicy()
                    configured = true
                    Log.d(TAG, "YouTube protocol initialized")
                }
            }
        }
    }

    fun warmUp(scope: CoroutineScope) {
        if (!policy.warmUpEnabled) return
        if (warmUpJob?.isActive == true) return

        warmUpJob = scope.launch(Dispatchers.IO) {
            try {
                ensureInitialized()

                val started = System.currentTimeMillis()
                withTimeout(policy.warmUpTimeoutMs) {
                    fetchWarmUpPage()
                }

                Log.d(TAG, "Warm-up OK in ${System.currentTimeMillis() - started}ms")
            } catch (t: Throwable) {
                Log.w(TAG, "Warm-up failed (non-critical): ${t.javaClass.simpleName} - ${t.message}")
            }
        }
    }

    private fun applyYoutubePolicy() {
        YoutubeStreamExtractor.setPoTokenProvider(null)
        YoutubeStreamExtractor.setFetchIosClient(policy.iosClientEnabled)

        Log.d(
            TAG,
            "Policy applied " +
                    "(iosClient=${policy.iosClientEnabled}, " +
                    "progressiveFallback=${policy.progressiveFallbackEnabled}, " +
                    "hlsFallback=${policy.hlsFallbackEnabled})"
        )
    }

    private suspend fun fetchWarmUpPage() = withContext(Dispatchers.IO) {
        val extractor = ServiceList.YouTube.getStreamExtractor(policy.warmUpUrl)
        extractor.fetchPage()
    }

    private companion object {
        const val TAG = "YoutubeProtocol"
    }
}