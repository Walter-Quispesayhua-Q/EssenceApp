package com.essence.essenceapp.feature.playback.manager

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evita que varias recuperaciones se disparen al mismo tiempo.
 *
 * Por ahora se usa para el refresh de fuente cuando Media3 avisa que la URL
 * expiro o que el decoder fallo de forma recuperable. Mas adelante puede
 * alojar reglas de reintento mas finas.
 */
enum class SourceRefreshStart {
    STARTED,
    ALREADY_RUNNING,
    EXHAUSTED
}

@Singleton
class PlaybackErrorRecoveryController @Inject constructor() {
    private var refreshingSource = false
    private var trackedHlsMasterKey: String? = null
    private var attemptsForCurrent: Int = 0

    fun tryStartSourceRefresh(hlsMasterKey: String?): SourceRefreshStart {
        val key = hlsMasterKey?.takeIf { it.isNotBlank() }
            ?: return SourceRefreshStart.EXHAUSTED

        if (refreshingSource) return SourceRefreshStart.ALREADY_RUNNING

        if (trackedHlsMasterKey != key) {
            trackedHlsMasterKey = key
            attemptsForCurrent = 0
        }

        if (attemptsForCurrent >= MAX_ATTEMPTS) {
            return SourceRefreshStart.EXHAUSTED
        }

        attemptsForCurrent++
        refreshingSource = true
        return SourceRefreshStart.STARTED
    }

    fun finishSourceRefresh() {
        refreshingSource = false
    }

    fun reset() {
        refreshingSource = false
        trackedHlsMasterKey = null
        attemptsForCurrent = 0
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
    }
}
