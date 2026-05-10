package com.essence.essenceapp.feature.song.ui.playback.manager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.essence.essenceapp.feature.song.ui.playback.service.MediaPlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PLAYBACK_SERVICE"

@Singleton
class PlaybackMediaServiceController @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun start() {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.d(TAG, "MediaPlaybackService iniciado")
        } catch (e: Exception) {
            Log.e(TAG, "Error iniciando MediaPlaybackService: ${e.message}", e)
        }
    }

    fun stop() {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java)
            context.stopService(intent)
            Log.d(TAG, "MediaPlaybackService detenido")
        } catch (e: Exception) {
            Log.e(TAG, "Error deteniendo MediaPlaybackService: ${e.message}", e)
        }
    }
}
