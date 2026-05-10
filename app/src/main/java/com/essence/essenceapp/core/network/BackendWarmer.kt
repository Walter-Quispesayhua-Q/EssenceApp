package com.essence.essenceapp.core.network

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

private const val TAG = "BackendWarmer"

@Singleton
class BackendWarmer @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    @Volatile
    private var warmUpJob: Job? = null

    fun warmUp(scope: CoroutineScope) {
        if (warmUpJob?.isActive == true) return
        warmUpJob = scope.launch(Dispatchers.IO) {
            val baseUrl = ApiConstants.BASE_URL.toHttpUrlOrNull()
            if (baseUrl == null) {
                Log.w(TAG, "Invalid BASE_URL for warmUp: ${ApiConstants.BASE_URL}")
                return@launch
            }
            prefetchDns(baseUrl.host)
            prefetchHandshake(baseUrl)
        }
    }

    private fun prefetchDns(host: String) {
        try {
            val started = System.currentTimeMillis()
            okHttpClient.dns.lookup(host)
            val elapsed = System.currentTimeMillis() - started
            Log.d(TAG, "DNS prefetch OK ($host) in ${elapsed}ms")
        } catch (e: Exception) {
            Log.w(TAG, "DNS prefetch failed (non-critical) [$host]: ${e.message}")
        }
    }

    private fun prefetchHandshake(baseUrl: okhttp3.HttpUrl) {
        try {
            val started = System.currentTimeMillis()
            val warmupUrl = baseUrl.newBuilder()
                .addPathSegment("search")
                .addPathSegment("categories")
                .build()
            val request = Request.Builder()
                .url(warmupUrl)
                .head()
                .build()
            okHttpClient.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - started
                Log.d(TAG, "Handshake prefetch OK (HTTP ${response.code}) in ${elapsed}ms")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Handshake prefetch failed (non-critical): ${e.message}")
        }
    }
}
