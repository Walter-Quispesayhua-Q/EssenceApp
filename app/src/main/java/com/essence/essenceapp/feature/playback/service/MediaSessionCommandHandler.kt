package com.essence.essenceapp.feature.playback.service

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.essence.essenceapp.R
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import javax.inject.Inject

/**
 * Maneja los comandos personalizados de la MediaSession.
 *
 * Actualmente se encarga del botón de like en la notificación y controles del
 * sistema, manteniendo esa lógica fuera del servicio principal.
 */
class MediaSessionCommandHandler @Inject constructor() {

    fun likeCommand(): SessionCommand =
        SessionCommand(LIKE_COMMAND, Bundle.EMPTY)

    fun buildLikeButton(isLiked: Boolean): CommandButton =
        CommandButton.Builder()
            .setDisplayName(if (isLiked) "Quitar de favoritos" else "Agregar a favoritos")
            .setIconResId(if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
            .setSessionCommand(likeCommand())
            .build()

    fun customLayout(isLiked: Boolean): List<CommandButton> =
        listOf(buildLikeButton(isLiked))

    fun handleCustomCommand(
        customCommand: SessionCommand,
        callbacks: MediaSessionCallbacks
    ): ListenableFuture<SessionResult> {
        if (customCommand.customAction == LIKE_COMMAND) {
            callbacks.onToggleLike()
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    companion object {
        const val LIKE_COMMAND = "ESSENCE_LIKE_TOGGLE"
    }
}