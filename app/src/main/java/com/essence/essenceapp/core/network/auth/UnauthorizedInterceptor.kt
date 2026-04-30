package com.essence.essenceapp.core.network.auth

import android.util.Log
import com.essence.essenceapp.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class UnauthorizedInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val hadAuthHeader = request.header("Authorization")?.startsWith("Bearer ") == true

        val response = chain.proceed(request)

        if (hadAuthHeader && AuthRoutePolicy.requiresAuth(path)) {
            when (response.code) {
                401 -> {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "401 en $path: sesion invalida")
                    }
                    sessionManager.onSessionExpired()
                }
                403, 404 -> {
                    if (isUserRoute(path)) {
                        if (BuildConfig.DEBUG) {
                            Log.w(TAG, "${response.code} en $path: usuario no existe, forzando logout")
                        }
                        sessionManager.onSessionExpired()
                    }
                }
            }
        }

        return response
    }

    private fun isUserRoute(path: String): Boolean {
        return path.contains("/user") ||
            path.contains("/profile") ||
            path.contains("/me")
    }

    companion object {
        private const val TAG = "UnauthorizedInterceptor"
    }
}