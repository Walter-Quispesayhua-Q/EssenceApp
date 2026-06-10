package com.essence.essenceapp.feature.song.data.repository.resolver.common

/**
 * Resultado de comprobar si una URL de streaming responde por HTTP.
 */
sealed interface StreamingUrlReachability {
    val isReachable: Boolean

    data class Reachable(val code: Int) : StreamingUrlReachability {
        override val isReachable: Boolean = true
    }

    data class Rejected(val code: Int) : StreamingUrlReachability {
        override val isReachable: Boolean = false
    }

    data class Failed(val error: Throwable) : StreamingUrlReachability {
        override val isReachable: Boolean = false
    }

    data class Invalid(val reason: String) : StreamingUrlReachability {
        override val isReachable: Boolean = false
    }
}