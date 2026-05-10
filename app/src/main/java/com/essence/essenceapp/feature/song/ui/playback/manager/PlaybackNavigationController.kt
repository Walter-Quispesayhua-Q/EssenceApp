package com.essence.essenceapp.feature.song.ui.playback.manager

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val NAVIGATION_DEBOUNCE_MS = 200L

@Singleton
class PlaybackNavigationController @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var pendingNavigationJob: Job? = null
    private var lastNavigationTime: Long = 0L

    fun navigate(action: () -> Unit) {
        val now = System.currentTimeMillis()
        val timeSinceLast = now - lastNavigationTime
        val isBurst = timeSinceLast <= NAVIGATION_DEBOUNCE_MS ||
                pendingNavigationJob?.isActive == true

        lastNavigationTime = now
        pendingNavigationJob?.cancel()

        if (!isBurst) {
            action()
            return
        }

        pendingNavigationJob = scope.launch {
            delay(NAVIGATION_DEBOUNCE_MS)
            action()
        }
    }

    fun cancelPending() {
        pendingNavigationJob?.cancel()
        pendingNavigationJob = null
    }
}
