package com.essence.essenceapp.feature.song.ui.playback.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.essence.essenceapp.MainActivity
import com.essence.essenceapp.R
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioPlayerEngine
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "MEDIA_SERVICE"
private const val SEEK_INCREMENT_MS = 10_000L

@AndroidEntryPoint
@OptIn(markerClass = [UnstableApi::class])
class MediaPlaybackService : MediaSessionService() {

    @Inject lateinit var audioPlayerEngine: AudioPlayerEngine
    @Inject lateinit var playbackManager: PlaybackManager

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var currentLikeState = false

    companion object {
        const val LIKE_COMMAND = "ESSENCE_LIKE_TOGGLE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: servicio iniciando")

        configureNotificationProvider()
        createOrUpdateSession()
        observePlayerChanges()
        observeLikeState()
    }

    private fun configureNotificationProvider() {
        val provider = DefaultMediaNotificationProvider.Builder(this)
            .setChannelName(R.string.media_notification_channel_name)
            .build()
            .apply { setSmallIcon(R.drawable.ic_logo_essence) }
        setMediaNotificationProvider(provider)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d(TAG, "onGetSession: session=${mediaSession != null}")
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "onTaskRemoved: isPlaybackOngoing=${isPlaybackOngoing()}")
        if (!isPlaybackOngoing()) {
            stopSelf()
        }
    }

    override fun isPlaybackOngoing(): Boolean {
        val player = mediaSession?.player ?: return false
        return player.isPlaying ||
                player.playbackState == Player.STATE_BUFFERING ||
                (player.playWhenReady && player.playbackState == Player.STATE_READY)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: liberando servicio")
        serviceScope.cancel()

        mediaSession?.let { session ->
            removeSession(session)
            session.release()
        }
        mediaSession = null

        super.onDestroy()
    }

    private fun createOrUpdateSession() {
        val player = audioPlayerEngine.getPlayerOrNull()
        if (player == null) {
            Log.w(TAG, "createOrUpdateSession: player es null")
            return
        }

        val existing = mediaSession
        val currentBasePlayer = (existing?.player as? ForwardingPlayer)
            ?.let(::getBasePlayer) ?: existing?.player

        if (existing != null && currentBasePlayer === player) {
            refreshCustomLayout()
            return
        }

        if (existing != null) {
            removeSession(existing)
            existing.release()
            Log.d(TAG, "Sesión anterior liberada (player cambió)")
        }

        val wrappedPlayer = createForwardingPlayer(player)

        val newSession = MediaSession.Builder(this, wrappedPlayer)
            .setSessionActivity(buildSessionActivity())
            .setCallback(SessionCallback())
            .build()

        mediaSession = newSession
        addSession(newSession)
        refreshCustomLayout()

        Log.d(TAG, "MediaSession creada con ForwardingPlayer")
    }

    private fun createForwardingPlayer(player: Player): ForwardingPlayer {
        return object : ForwardingPlayer(player) {

            override fun getAvailableCommands(): Player.Commands {
                return super.availableCommands
                    .buildUpon()
                    .add(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    .add(COMMAND_SEEK_TO_NEXT)
                    .add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .add(COMMAND_SEEK_TO_PREVIOUS)
                    .add(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .build()
            }

            override fun isCommandAvailable(command: Int): Boolean {
                return when (command) {
                    COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM,
                    COMMAND_SEEK_TO_NEXT,
                    COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
                    COMMAND_SEEK_TO_PREVIOUS,
                    COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> true
                    else -> super.isCommandAvailable(command)
                }
            }

            override fun getCurrentPosition(): Long {
                val rawPosition = super.currentPosition.coerceAtLeast(0L)
                val effectiveDuration = resolveEffectiveDuration(super.duration)
                return if (effectiveDuration == C.TIME_UNSET) {
                    rawPosition
                } else {
                    rawPosition.coerceAtMost(effectiveDuration)
                }
            }

            override fun getBufferedPosition(): Long {
                val rawBuffered = super.bufferedPosition.coerceAtLeast(0L)
                val effectiveDuration = resolveEffectiveDuration(super.duration)
                return if (effectiveDuration == C.TIME_UNSET) {
                    rawBuffered
                } else {
                    rawBuffered.coerceAtMost(effectiveDuration)
                }
            }

            override fun getDuration(): Long {
                return resolveEffectiveDuration(super.duration)
            }

            override fun getSeekBackIncrement(): Long = SEEK_INCREMENT_MS

            override fun getSeekForwardIncrement(): Long = SEEK_INCREMENT_MS

            override fun seekBack() {
                val target = (currentPosition - seekBackIncrement).coerceAtLeast(0L)
                seekTo(target)
            }

            override fun seekForward() {
                val current = currentPosition
                val effectiveDuration = duration
                val forward = current + seekForwardIncrement
                val target = if (effectiveDuration == C.TIME_UNSET || effectiveDuration <= 0L) {
                    forward
                } else {
                    forward.coerceAtMost(effectiveDuration)
                }
                seekTo(target)
            }

            override fun seekTo(positionMs: Long) {
                val safePosition = positionMs.coerceAtLeast(0L)
                val effectiveDuration = duration
                if (effectiveDuration == C.TIME_UNSET || effectiveDuration <= 0L) {
                    super.seekTo(safePosition)
                } else {
                    super.seekTo(safePosition.coerceAtMost(effectiveDuration))
                }
            }

            override fun seekToNext() {
                Log.d(TAG, "ForwardingPlayer: seekToNext")
                playbackManager.onAction(PlaybackAction.Next)
            }

            override fun seekToNextMediaItem() {
                Log.d(TAG, "ForwardingPlayer: seekToNextMediaItem")
                playbackManager.onAction(PlaybackAction.Next)
            }

            override fun seekToPrevious() {
                Log.d(TAG, "ForwardingPlayer: seekToPrevious")
                playbackManager.onAction(PlaybackAction.Previous)
            }

            override fun seekToPreviousMediaItem() {
                Log.d(TAG, "ForwardingPlayer: seekToPreviousMediaItem")
                playbackManager.onAction(PlaybackAction.Previous)
            }

            override fun hasNextMediaItem(): Boolean =
                playbackManager.uiState.value.canGoNext

            override fun hasPreviousMediaItem(): Boolean =
                playbackManager.uiState.value.canGoPrevious
        }
    }

    private fun getBasePlayer(forwardingPlayer: ForwardingPlayer): Player {
        var current: Player = forwardingPlayer
        while (current is ForwardingPlayer) {
            current = current.wrappedPlayer
        }
        return current
    }

    private fun resolveEffectiveDuration(playerDurationMs: Long): Long {
        if (playerDurationMs != C.TIME_UNSET && playerDurationMs >= 0L) return playerDurationMs
        val fallback = playbackManager.nowPlaying.value?.durationMs ?: 0L
        return if (fallback > 0L) fallback else C.TIME_UNSET
    }

    @UnstableApi
    private fun observePlayerChanges() {
        serviceScope.launch {
            // Solo reaccionar cuando cambia la cancion real (songLookup), no
            // cuando se actualizan campos secundarios (durationMs parcial, etc).
            playbackManager.nowPlaying
                .distinctUntilChanged { old, new -> old?.songLookup == new?.songLookup }
                .collectLatest { now ->
                    if (now == null) return@collectLatest
                    createOrUpdateSession()
                    Log.d(TAG, "observePlayerChanges: sesion verificada para ${now.songLookup}")
                }
        }
    }

    private fun observeLikeState() {
        serviceScope.launch {
            playbackManager.isCurrentSongLiked.collectLatest { isLiked ->
                currentLikeState = isLiked
                refreshCustomLayout()
                Log.d(TAG, "observeLikeState: isLiked=$isLiked")
            }
        }
    }

    private fun refreshCustomLayout() {
        mediaSession?.setCustomLayout(listOf(buildLikeButton(currentLikeState)))
    }

    private fun buildSessionActivity(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
            ?: Intent(this, MainActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        return PendingIntent.getActivity(
            this,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildLikeButton(isLiked: Boolean): CommandButton {
        return CommandButton.Builder()
            .setDisplayName(if (isLiked) "Quitar de favoritos" else "Agregar a favoritos")
            .setIconResId(if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
            .setSessionCommand(SessionCommand(LIKE_COMMAND, Bundle.EMPTY))
            .build()
    }

    private inner class SessionCallback : MediaSession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val likeCommand = SessionCommand(LIKE_COMMAND, Bundle.EMPTY)

            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                .buildUpon()
                .add(likeCommand)
                .build()

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .setCustomLayout(listOf(buildLikeButton(currentLikeState)))
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == LIKE_COMMAND) {
                Log.d(TAG, "Like toggle desde notificación")
                playbackManager.onAction(PlaybackAction.ToggleLike)
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }
}