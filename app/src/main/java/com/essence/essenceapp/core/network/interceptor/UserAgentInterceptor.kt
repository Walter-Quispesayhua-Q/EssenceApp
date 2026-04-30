package com.essence.essenceapp.core.network.interceptor

import android.os.Build
import com.essence.essenceapp.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class UserAgentInterceptor @Inject constructor() : Interceptor {

    private val userAgent: String by lazy {
        buildString {
            append(BuildConfig.APPLICATION_ID)
            append('/')
            append(BuildConfig.VERSION_NAME)
            append(" (Android ")
            append(Build.VERSION.RELEASE)
            append("; SDK ")
            append(Build.VERSION.SDK_INT)
            append("; ")
            append(Build.MANUFACTURER)
            append(' ')
            append(Build.MODEL)
            append(')')
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.header("User-Agent") != null) {
            return chain.proceed(request)
        }
        val withUa = request.newBuilder()
            .header("User-Agent", userAgent)
            .header("X-Client-Platform", "android")
            .header("X-Client-Version", BuildConfig.VERSION_NAME)
            .header("X-Client-Build", BuildConfig.VERSION_CODE.toString())
            .build()
        return chain.proceed(withUa)
    }
}
