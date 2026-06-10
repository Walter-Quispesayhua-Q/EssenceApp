package com.essence.essenceapp.core.streaming

import java.time.Instant

object ExtractorTimeStreamUrl {

    private val EXPIRE_REGEX = Regex("[?&]expire=(\\d+)")

    fun expireFrom(url: String?): Instant? {
        if (url.isNullOrBlank()) return null
        val match = EXPIRE_REGEX.find(url) ?: return null
        return runCatching { Instant.ofEpochSecond(match.groupValues[1].toLong()) }
            .getOrNull()
    }
}
