package com.essence.essenceapp.feature.playback.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.essence.essenceapp.MainActivity
import com.essence.essenceapp.R
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val TAG = "MEDIA_SERVICE"

/**
 * Servicio Android que mantiene viva la reproducción en segundo plano.
 *
 * Es el dueño real de ExoPlayer y MediaSession. Expone esa sesión al sistema
 * para que funcionen notificación, lock screen, Bluetooth y otros controles
 * externos sin que las pantallas dependan directamente de Media3.
 */
@AndroidEntryPoint
@OptIn(UnstableApi::class)
class MediaPlaybackService : MediaSessionService() {

    @Inject lateinit var mediaSessionController: MediaSessionController
    @Inject lateinit var playbackController: PlaybackController
    @Inject lateinit var exoPlayerFactory: ExoPlayerFactory

    private var player: ExoPlayer? = null
    private var currentLikedState: Boolean = false
    private var taskRemoved: Boolean = false

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()

        configureNotificationProvider()

        val exoPlayer = exoPlayerFactory.create(useAuthHeader = false)
        player = exoPlayer

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (taskRemoved && !playWhenReady) {
                    Log.d(TAG, "Playback paused after task removed. Stopping service.")
                    stopSelf()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (taskRemoved && (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE)) {
                    Log.d(TAG, "Playback ended/idle after task removed. Stopping service.")
                    stopSelf()
                }
            }
        })

        mediaSessionController.createOrReplace(
            context = this,
            player = exoPlayer,
            sessionActivity = buildSessionActivity(),
            callbacks = buildCallbacks(),
            isLiked = currentLikedState
        )

        observeLikeState()

        Log.d(TAG, "MediaPlaybackService created")
    }

    private fun observeLikeState() {
        playbackController.nowPlaying
            .map { nowPlaying -> nowPlaying?.isLiked == true }
            .distinctUntilChanged()
            .onEach { isLiked ->
                currentLikedState = isLiked
                mediaSessionController.updateLikeState(isLiked)
            }
            .launchIn(serviceScope)
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? =
        mediaSessionController.current()

    override fun onTaskRemoved(rootIntent: Intent?) {
        taskRemoved = true
        super.onTaskRemoved(rootIntent)
        if (!isPlaybackOngoing()) {
            Log.d(TAG, "Task removed and playback not ongoing. Stopping service.")
            stopSelf()
        }
    }

    override fun isPlaybackOngoing(): Boolean {
        val currentPlayer = player ?: return false

        return currentPlayer.isPlaying ||
                currentPlayer.playbackState == Player.STATE_BUFFERING ||
                (currentPlayer.playWhenReady && currentPlayer.playbackState == Player.STATE_READY)
    }

    override fun onDestroy() {
        Log.d(TAG, "MediaPlaybackService destroyed")

        mediaSessionController.release()

        player?.release()
        player = null

        serviceScope.cancel()

        super.onDestroy()
    }

    private fun configureNotificationProvider() {
        val provider = DefaultMediaNotificationProvider.Builder(this)
            .setChannelName(R.string.media_notification_channel_name)
            .build()
            .apply {
                setSmallIcon(R.drawable.ic_logo_essence)
            }

        setMediaNotificationProvider(provider)
    }

    private fun buildSessionActivity(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
            ?: Intent(this, MainActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        return PendingIntent.getActivity(
            this,
            SESSION_ACTIVITY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildCallbacks(): MediaSessionCallbacks =
        object : MediaSessionCallbacks {
            override fun onNext() {
                playbackController.dispatch(PlaybackAction.Next)
            }

            override fun onPrevious() {
                playbackController.dispatch(PlaybackAction.Previous)
            }

            override fun onToggleLike() {
                val songId = playbackController.nowPlaying.value
                    ?.item
                    ?.songId
                    ?: playbackController.currentSong.value?.id
                    ?: return

                playbackController.dispatch(PlaybackAction.ToggleLike(songId))
            }

            override fun canSkipNext(): Boolean =
                playbackController.nowPlaying.value?.canSkipNext == true

            override fun canSkipPrevious(): Boolean =
                playbackController.nowPlaying.value?.canSkipPrevious == true

            override fun currentDurationMs(): Long =
                playbackController.position.value.durationMs
                    .takeIf { it > 0L }
                    ?: playbackController.nowPlaying.value?.item?.durationMs
                    ?: 0L
        }

    companion object {
        private const val SESSION_ACTIVITY_REQUEST_CODE = 1001
    }
}