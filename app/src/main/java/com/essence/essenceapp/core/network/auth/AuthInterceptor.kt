package com.essence.essenceapp.core.network.auth

import com.essence.essenceapp.core.storage.TokenManager
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (request.header("Authorization") != null || !AuthRoutePolicy.requiresAuth(path)) {
            return chain.proceed(request)
        }

        val token = tokenManager.getCachedToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val authenticated = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authenticated)
    }
}