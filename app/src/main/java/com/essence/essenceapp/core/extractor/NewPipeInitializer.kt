package com.essence.essenceapp.core.extractor

import android.util.Log
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.concurrent.TimeUnit

object NewPipeInitializer {

    private const val TAG = "NewPipeInit"

    @Volatile
    private var initialized = false

    fun init() {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    NewPipe.init(
                        EssenceDownloader(),
                        Localization("en", "US"),
                        ContentCountry("US")
                    )
                    initialized = true
                    Log.d(TAG, "NewPipe initialized")
                }
            }
        }
    }

    private class EssenceDownloader : Downloader() {

        companion object {
            private const val USER_AGENT =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0"

            private const val YOUTUBE_CONSENT_COOKIE = "SOCS=CAISNQgDEitib3FfaWRlbnRpdHlmcm9udGVuZHVpc2VydmVyXzIwMjMwODI5LjA3X3AxGgJlbiACGgYIgJnOoQY"
            private const val YOUTUBE_DOMAIN = "youtube.com"
        }

        private val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(10, 5, TimeUnit.MINUTES))
            .dispatcher(
                Dispatcher().apply {
                    maxRequests = 32
                    maxRequestsPerHost = 16
                }
            )
            .build()

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
