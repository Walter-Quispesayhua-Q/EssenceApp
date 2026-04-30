package com.essence.essenceapp.core.network.interceptor

import android.util.Log
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.method
        val isRetryableMethod = method == "GET" || method == "HEAD"

        var attempt = 0
        var lastError: IOException? = null
        var lastResponse: Response? = null

        while (attempt <= MAX_RETRIES) {
            try {
                lastResponse?.close()
                val response = chain.proceed(request)

                if (response.isSuccessful || response.code in NON_RETRYABLE_STATUSES) {
                    return response
                }

                if (response.code in RETRYABLE_STATUSES && isRetryableMethod && attempt < MAX_RETRIES) {
                    Log.w(TAG, "HTTP ${response.code} on ${request.url.encodedPath}, retry ${attempt + 1}/$MAX_RETRIES")
                    lastResponse = response
                    sleepWithBackoff(attempt)
                    attempt++
                    continue
                }

                return response
            } catch (e: IOException) {
                lastError = e
                if (!isRetryableMethod || attempt >= MAX_RETRIES) {
                    throw e
                }
                Log.w(TAG, "IOException on ${request.url.encodedPath}: ${e.message}, retry ${attempt + 1}/$MAX_RETRIES")
                sleepWithBackoff(attempt)
                attempt++
            }
        }

        lastResponse?.let { return it }
        throw lastError ?: IOException("Retry exhausted without response")
    }

    private fun sleepWithBackoff(attempt: Int) {
        val exponentialMs = INITIAL_BACKOFF_MS * 2.0.pow(attempt.toDouble()).toLong()
        val cappedMs = min(exponentialMs, MAX_BACKOFF_MS)
        val jitterMs = Random.nextLong(0, JITTER_MS)
        val totalMs = cappedMs + jitterMs

        try {
            Thread.sleep(totalMs)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Retry interrupted", e)
        }
    }

    companion object {
        private const val TAG = "RetryInterceptor"
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 400L
        private const val MAX_BACKOFF_MS = 4_000L
        private const val JITTER_MS = 250L

        private val RETRYABLE_STATUSES = setOf(408, 425, 429, 500, 502, 503, 504)
        private val NON_RETRYABLE_STATUSES = setOf(400, 401, 403, 404, 405, 409, 410, 422)
    }
}
