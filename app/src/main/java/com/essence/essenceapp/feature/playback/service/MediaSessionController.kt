package com.essence.essenceapp.feature.playback.service

import android.app.PendingIntent
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orquesta la MediaSession activa del servicio.
 *
 * Mantiene una sola sesión viva, la crea cuando el servicio entrega un player
 * válido y actualiza partes pequeñas como el botón de like sin recrearla.
 */
@Singleton
class MediaSessionController @Inject constructor(
    private val sessionFactory: MediaSessionFactory,
    private val commandHandler: MediaSessionCommandHandler
) {
    private var mediaSession: MediaSession? = null

    fun createOrReplace(
        context: Context,
        player: Player,
        sessionActivity: PendingIntent,
        callbacks: MediaSessionCallbacks,
        isLiked: Boolean
    ): MediaSession {
        release()

        return sessionFactory.create(
            context = context,
            player = player,
            sessionActivity = sessionActivity,
            callbacks = callbacks,
            isLiked = isLiked
        ).also { session ->
            mediaSession = session
        }
    }

    fun current(): MediaSession? =
        mediaSession

    fun updateLikeState(isLiked: Boolean) {
        mediaSession?.setCustomLayout(
            commandHandler.customLayout(isLiked)
        )
    }

    fun release() {
        mediaSession?.release()
        mediaSession = null
    }
}