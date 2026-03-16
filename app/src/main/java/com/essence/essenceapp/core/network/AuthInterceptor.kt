package com.essence.essenceapp.core.network

import com.essence.essenceapp.core.network.storage.TokenManager
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.token.first()
        }

        val request = chain.request()
        val path = request.url.encodedPath
        val requestBuilder = request.newBuilder()

        val isPublicAuthRoute =
            path.endsWith("/api/v1/login") ||
                    path.endsWith("/api/v1/register")

        if (!token.isNullOrBlank() && !isPublicAuthRoute) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}