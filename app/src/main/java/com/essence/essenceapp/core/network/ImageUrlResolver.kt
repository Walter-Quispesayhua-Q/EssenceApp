package com.essence.essenceapp.core.network

import android.net.Uri
import android.util.Log

private const val TAG = "ImageUrlResolver"

fun resolveImageUrl(imageKey: String?): String? {
    if (imageKey.isNullOrBlank()) return null

    val trimmed = imageKey.trim()
    if (trimmed.isEmpty()) return null

    return when {
        trimmed.startsWith("https://", ignoreCase = true) -> trimmed
        trimmed.startsWith("http://", ignoreCase = true) -> trimmed
        trimmed.startsWith("//") -> "https:$trimmed"
        trimmed.startsWith("data:", ignoreCase = true) -> trimmed
        trimmed.startsWith("/") -> resolveAgainstHost(trimmed)
        else -> resolveAgainstBaseUrl(trimmed)
    }
}

private val baseHostUrl: String? by lazy {
    runCatching {
        val uri = Uri.parse(ApiConstants.BASE_URL)
        val scheme = uri.scheme ?: return@runCatching null
        val host = uri.host ?: return@runCatching null
        val portSegment = if (uri.port != -1) ":${uri.port}" else ""
        "$scheme://$host$portSegment"
    }.getOrNull()
}

private val baseUrlNormalized: String by lazy {
    ApiConstants.BASE_URL.trimEnd('/')
}

private fun resolveAgainstHost(absolutePath: String): String? {
    val host = baseHostUrl
    if (host == null) {
        Log.w(TAG, "No se pudo derivar host desde BASE_URL para path: $absolutePath")
        return null
    }
    return "$host$absolutePath"
}

private fun resolveAgainstBaseUrl(relativePath: String): String? {
    return try {
        "$baseUrlNormalized/$relativePath"
    } catch (e: Exception) {
        Log.w(TAG, "No se pudo resolver path relativo: $relativePath", e)
        null
    }
}