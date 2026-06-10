package com.essence.essenceapp.feature.song.data.repository.resolver.common

import com.essence.essenceapp.core.streaming.ExtractorTimeStreamUrl
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Decide si una URL de streaming todavia se puede usar para reproducir.
 */
@Singleton
class StreamingUrlValidator @Inject constructor(
    private val clock: Clock
) {

    fun isFresh(
        url: String?,
        expiresAt: Instant?,
        buffer: Duration = DEFAULT_BUFFER
    ): Boolean {
        if (url.isNullOrBlank()) return false
        val resolvedExpiresAt = expiresAt
            ?: ExtractorTimeStreamUrl.expireFrom(url)
            ?: return true
        return resolvedExpiresAt.isAfter(clock.instant().plus(buffer))
    }

    companion object {
        val DEFAULT_BUFFER: Duration = Duration.ofSeconds(60)
    }
}
