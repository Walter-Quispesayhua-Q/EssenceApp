package com.essence.essenceapp.feature.playback.service

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.exoplayer.util.EventLogger
import com.essence.essenceapp.BuildConfig
import com.essence.essenceapp.feature.playback.cache.CacheDataSourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AUDIO_DEBUG"

/**
 * Crea instancias de ExoPlayer configuradas para audio en streaming.
 *
 * Usa el pipeline de cache/red definido en playback cache y deja listo el
 * player para MediaSession, notificación, audio focus y reproducción en fondo.
 */
@Singleton
@OptIn(UnstableApi::class)
class ExoPlayerFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheDataSourceProvider: CacheDataSourceProvider
) {

    fun create(useAuthHeader: Boolean): ExoPlayer {
        val dataSourceFactory = cacheDataSourceProvider.createFactory(useAuthHeader)

        val loadErrorPolicy = object : DefaultLoadErrorHandlingPolicy() {
            override fun getRetryDelayMsFor(
                loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo
            ): Long {
                return when (loadErrorInfo.errorCount) {
                    1 -> 500L
                    2 -> 1_500L
                    3 -> 3_000L
                    else -> C.TIME_UNSET
                }
            }
        }

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setLoadErrorHandlingPolicy(loadErrorPolicy)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                30_000,
                60_000,
                1_500,
                5_000
            )
            .setBackBuffer(30_000, true)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableAudioFloatOutput(false)
            .setEnableDecoderFallback(true)
            .forceDisableMediaCodecAsynchronousQueueing()

        return ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                skipSilenceEnabled = false
                installDiagnostics()
            }
    }

    fun shouldAttachAuthHeader(url: String): Boolean =
        cacheDataSourceProvider.shouldAttachAuthHeader(url)

    private fun ExoPlayer.installDiagnostics() {
        if (BuildConfig.DEBUG) {
            addAnalyticsListener(EventLogger())
        }

        addAnalyticsListener(object : AnalyticsListener {
            override fun onAudioSessionIdChanged(
                eventTime: AnalyticsListener.EventTime,
                audioSessionId: Int
            ) {
                Log.d(TAG, "audioSessionId=$audioSessionId")
            }
        })

        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateName = when (playbackState) {
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> "READY"
                    Player.STATE_ENDED -> "ENDED"
                    else -> "UNKNOWN($playbackState)"
                }

                Log.d(TAG, "state=$stateName pos=${currentPosition}ms")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "isPlaying=$isPlaying pos=${currentPosition}ms")
            }
        })
    }
}
