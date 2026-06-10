package com.essence.essenceapp.feature.playback.observers

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Arranca los observers pasivos de playback.
 *
 * Estos observers solo escuchan el estado publico de reproduccion para registrar
 * metricas o historial. Se centralizan aqui para no mezclarlos dentro del
 * controller principal.
 */
@Singleton
class PlaybackObserversStarter @Inject constructor(
    private val ttfpObserver: PlaybackTtfpObserver,
    private val historyObserver: PlaybackHistoryObserver,
    private val proactiveRefreshObserver: PlaybackProactiveRefreshObserver
) {
    private var started = false

    fun start() {
        if (started) return

        started = true
        ttfpObserver.start()
        historyObserver.start()
        proactiveRefreshObserver.start()
    }
}
