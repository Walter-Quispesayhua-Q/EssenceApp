package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PLAYBACK_TTFP"

@Singleton
class PlaybackTtfpTracker @Inject constructor() {

    private val timers = ConcurrentHashMap<String, Long>()

    fun start(lookup: String, source: String, replaceExisting: Boolean = false) {
        if (replaceExisting) {
            timers[lookup] = System.currentTimeMillis()
            Log.d(TAG, "Timer start [$source]: $lookup (replace)")
            return
        }
        val previous = timers.putIfAbsent(lookup, System.currentTimeMillis())
        if (previous == null) {
            Log.d(TAG, "Timer start [$source]: $lookup")
        }
    }

    fun reportIfPending(lookup: String?) {
        val key = lookup ?: return
        val start = timers.remove(key) ?: return
        val ttfp = System.currentTimeMillis() - start
        Log.d(TAG, "TTFP=${ttfp}ms for $key")
    }

    fun clear() {
        timers.clear()
    }
}
