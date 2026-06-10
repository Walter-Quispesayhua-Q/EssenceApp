package com.essence.essenceapp.core.extractor

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.concurrent.TimeUnit

object NewPipeInitializer {

    private const val TAG = "NewPipeInit"
    private const val WARM_UP_VIDEO_URL = "https://music.youtube.com/watch?v=dQw4w9WgXcQ"

    @Volatile
    private var initialized = false

    @Volatile
    private var warmUpJob: Job? = null

    @Volatile
    private var injectedClient: OkHttpClient? = null

    fun setOkHttpClient(client: OkHttpClient) {
        injectedClient = client
    }

    fun init() {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    NewPipe.init(
                        EssenceDownloader(resolveClient()),
                        Localization("en", "US"),
                        ContentCountry("US")
                    )
                    initialized = true
                    Log.d(TAG, "NewPipe initialized (sharedClient=${injectedClient != null}, iosClient=false)")
                }
            }
        }
    }

    fun warmUp(scope: CoroutineScope) {
        if (warmUpJob?.isActive == true) return
        warmUpJob = scope.launch(Dispatchers.IO) {
            try {
                init()
                val started = System.currentTimeMillis()
                val extractor = ServiceList.YouTube.getStreamExtractor(WARM_UP_VIDEO_URL)
                extractor.fetchPage()
                val elapsed = System.currentTimeMillis() - started
                Log.d(TAG, "Warm-up OK in ${elapsed}ms")
            } catch (t: Throwable) {
                Log.w(TAG, "Warm-up failed (non-critical): ${t.javaClass.simpleName} - ${t.message}")
            }
        }
    }

    private fun resolveClient(): OkHttpClient {
        return injectedClient ?: defaultClient()
    }

    private fun defaultClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    private class EssenceDownloader(
        private val client: OkHttpClient
    ) : Downloader() {

        companion object {
            private const val USER_AGENT =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0"

            private const val YOUTUBE_CONSENT_COOKIE = "SOCS=CAISNQgDEitib3FfaWRlbnRpdHlmcm9udGVuZHVpc2VydmVyXzIwMjMwODI5LjA3X3AxGgJlbiACGgYIgJnOoQY"
            private const val YOUTUBE_DOMAIN = "youtube.com"
        }

        override fun execute(request: Request): Response {
            val httpMethod = request.httpMethod()
            val url = request.url()
            val headers = request.headers()
            val dataToSend = request.dataToSend()

            // Body (para POST, PUT, etc.)
            val requestBody = dataToSend?.toRequestBody()

            val requestBuilder = okhttp3.Request.Builder()
                .method(httpMethod, requestBody)
                .url(url)
                .addHeader("User-Agent", USER_AGENT)

            // Cookie de consent para dominios de YouTube
            if (url.contains(YOUTUBE_DOMAIN)) {
                requestBuilder.addHeader("Cookie", YOUTUBE_CONSENT_COOKIE)
            }

            // Headers del request original de NewPipe
            headers.forEach { (headerName, headerValues) ->
                requestBuilder.removeHeader(headerName)
                headerValues.forEach { value ->
                    requestBuilder.addHeader(headerName, value)
                }
            }

            // Ejecutar request
            client.newCall(requestBuilder.build()).execute().use { response ->
                if (response.code == 429) {
                    throw ReCaptchaException("reCaptcha Challenge requested", url)
                }

                // Leer body
                val responseBody = response.body?.string() ?: ""

                // URL final después de redirects
                val latestUrl = response.request.url.toString()

                return Response(
                    response.code,
                    response.message,
                    response.headers.toMultimap(),
                    responseBody,
                    latestUrl
                )
            }
        }
    }
}
