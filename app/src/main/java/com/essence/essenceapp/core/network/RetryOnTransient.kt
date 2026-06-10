package com.essence.essenceapp.core.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> retryOnTransient(
    maxAttempts: Int = 3,
    initialDelayMs: Long = 600L,
    backoffFactor: Double = 2.0,
    onAttemptFailed: (suspend (attempt: Int, cause: Throwable) -> Unit)? = null,
    block: suspend () -> T
): T {
    require(maxAttempts >= 1) { "maxAttempts must be >= 1" }
    var currentDelay = initialDelayMs

    repeat(maxAttempts - 1) { attemptIndex ->
        try {
            return block()
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Throwable) {
            if (!isRetriable(e)) throw e
            onAttemptFailed?.invoke(attemptIndex + 1, e)
            delay(currentDelay)
            currentDelay = (currentDelay * backoffFactor).toLong()
        }
    }
    return try {
        block()
    } catch (ce: CancellationException) {
        throw ce
    } catch (e: Throwable) {
        onAttemptFailed?.invoke(maxAttempts, e)
        throw e
    }
}

private fun isRetriable(e: Throwable): Boolean = when (e) {
    is IOException -> true
    is HttpException -> e.code() in RETRIABLE_HTTP_CODES
    else -> false
}

private val RETRIABLE_HTTP_CODES: Set<Int> =
    (500..599).toSet() + setOf(408, 429)
