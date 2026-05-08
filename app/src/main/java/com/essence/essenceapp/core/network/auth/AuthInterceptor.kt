package com.essence.essenceapp.core.network.auth

import com.essence.essenceapp.core.storage.TokenManager
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (request.header("Authorization") != null || !AuthRoutePolicy.requiresAuth(path)) {
            return chain.proceed(request)
        }

        val token = tokenManager.getCachedToken()
        if (token.isNullOrBlank()) {
            sessionManager.onAuthRequired()
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Auth required (synthetic)")
                .body("""{"error":"auth_required"}""".toResponseBody(JSON_MEDIA_TYPE))
                .build()
        }

        val authenticated = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authenticated)
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}