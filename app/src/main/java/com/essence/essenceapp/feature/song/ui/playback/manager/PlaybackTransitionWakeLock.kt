package com.essence.essenceapp.feature.song.ui.playback.manager

import android.content.Context
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val WAKELOCK_TAG = "EssenceApp:PlaybackTransitionLock"
private const val WAKELOCK_TIMEOUT_MS = 30_000L

@Singleton
class PlaybackTransitionWakeLock @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val powerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private val wakeLock by lazy {
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG).apply {
            setReferenceCounted(false)
        }
    }

    fun acquire() {
        if (!wakeLock.isHeld) {
            wakeLock.acquire(WAKELOCK_TIMEOUT_MS)
        }
    }

    fun release() {
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}
