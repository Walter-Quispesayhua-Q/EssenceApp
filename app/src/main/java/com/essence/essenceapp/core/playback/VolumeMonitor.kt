package com.essence.essenceapp.core.playback

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "VOLUME_MONITOR"

@Singleton
class VolumeMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isMuted = MutableStateFlow(detectMuted())
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val volumeObserver = object : ContentObserver(mainHandler) {
        override fun onChange(selfChange: Boolean) {
            refresh()
        }
    }

    init {
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )
        Log.d(TAG, "Initial muted=${_isMuted.value}")
    }

    fun release() {
        context.contentResolver.unregisterContentObserver(volumeObserver)
    }

    private fun refresh() {
        val muted = detectMuted()
        if (muted != _isMuted.value) {
            Log.d(TAG, "Muted changed: ${_isMuted.value} -> $muted")
            _isMuted.value = muted
        }
    }

    private fun detectMuted(): Boolean {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
    }
}
