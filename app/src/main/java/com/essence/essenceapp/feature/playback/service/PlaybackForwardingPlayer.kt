package com.essence.essenceapp.feature.playback.service

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

private const val SEEK_INCREMENT_MS = 10_000L

/**
 * Adaptador del Player usado por la MediaSession.
 *
 * Mantiene el control real en ExoPlayer, pero redirige acciones externas
 * como siguiente, anterior y saltos de tiempo hacia el controlador de playback.
 */
@OptIn(UnstableApi::class)
class PlaybackForwardingPlayer(
    player: Player,
    private val callbacks: MediaSessionCallbacks
) : ForwardingPlayer(player) {

    override fun getAvailableCommands(): Player.Commands =
        super.availableCommands
            .buildUpon()
            .add(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
            .add(COMMAND_SEEK_TO_NEXT)
            .add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
            .add(COMMAND_SEEK_TO_PREVIOUS)
            .add(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
            .build()

    override fun isCommandAvailable(command: Int): Boolean =
        when (command) {
            COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM,
            COMMAND_SEEK_TO_NEXT,
            COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
            COMMAND_SEEK_TO_PREVIOUS,
            COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> true
            else -> super.isCommandAvailable(command)
        }

    override fun getSeekBackIncrement(): Long =
        SEEK_INCREMENT_MS

    override fun getSeekForwardIncrement(): Long =
        SEEK_INCREMENT_MS

    override fun getCurrentPosition(): Long {
        val position = super.currentPosition.coerceAtLeast(0L)
        val duration = resolveDuration(super.duration)

        return if (duration == C.TIME_UNSET) {
            position
        } else {
            position.coerceAtMost(duration)
        }
    }

    override fun getBufferedPosition(): Long {
        val buffered = super.bufferedPosition.coerceAtLeast(0L)
        val duration = resolveDuration(super.duration)

        return if (duration == C.TIME_UNSET) {
            buffered
        } else {
            buffered.coerceAtMost(duration)
        }
    }

    override fun getDuration(): Long =
        resolveDuration(super.duration)

    override fun seekBack() {
        seekTo((currentPosition - seekBackIncrement).coerceAtLeast(0L))
    }

    override fun seekForward() {
        val target = currentPosition + seekForwardIncrement
        val duration = duration

        if (duration == C.TIME_UNSET || duration <= 0L) {
            seekTo(target)
        } else {
            seekTo(target.coerceAtMost(duration))
        }
    }

    override fun seekTo(positionMs: Long) {
        val safePosition = positionMs.coerceAtLeast(0L)
        val duration = duration

        if (duration == C.TIME_UNSET || duration <= 0L) {
            super.seekTo(safePosition)
        } else {
            super.seekTo(safePosition.coerceAtMost(duration))
        }
    }

    override fun seekToNext() {
        callbacks.onNext()
    }

    override fun seekToNextMediaItem() {
        callbacks.onNext()
    }

    override fun seekToPrevious() {
        callbacks.onPrevious()
    }

    override fun seekToPreviousMediaItem() {
        callbacks.onPrevious()
    }

    override fun hasNextMediaItem(): Boolean =
        callbacks.canSkipNext()

    override fun hasPreviousMediaItem(): Boolean =
        callbacks.canSkipPrevious()

    private fun resolveDuration(playerDurationMs: Long): Long {
        if (playerDurationMs != C.TIME_UNSET && playerDurationMs >= 0L) {
            return playerDurationMs
        }

        val fallback = callbacks.currentDurationMs()
        return if (fallback > 0L) fallback else C.TIME_UNSET
    }
}