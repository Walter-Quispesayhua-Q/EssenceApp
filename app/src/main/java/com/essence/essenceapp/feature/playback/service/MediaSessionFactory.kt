package com.essence.essenceapp.feature.playback.service

import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import javax.inject.Inject

/**
 * Construye la MediaSession usada por el servicio de reproducción.
 *
 * Recibe el player real, lo adapta para controles externos y registra los
 * comandos personalizados que la notificación o el sistema pueden enviar.
 */
class MediaSessionFactory @Inject constructor(
    private val commandHandler: MediaSessionCommandHandler
) {

    fun create(
        context: Context,
        player: Player,
        sessionActivity: PendingIntent,
        callbacks: MediaSessionCallbacks,
        isLiked: Boolean
    ): MediaSession {
        val forwardingPlayer = PlaybackForwardingPlayer(
            player = player,
            callbacks = callbacks
        )

        return MediaSession.Builder(context, forwardingPlayer)
            .setSessionActivity(sessionActivity)
            .setCallback(SessionCallback(callbacks, isLiked))
            .build()
    }

    private inner class SessionCallback(
        private val callbacks: MediaSessionCallbacks,
        private val initialLiked: Boolean
    ) : MediaSession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                .buildUpon()
                .add(commandHandler.likeCommand())
                .build()

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .setCustomLayout(commandHandler.customLayout(initialLiked))
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> =
            commandHandler.handleCustomCommand(
                customCommand = customCommand,
                callbacks = callbacks
            )
    }
}