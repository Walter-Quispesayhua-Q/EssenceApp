package com.essence.essenceapp.feature.song.ui.components.playbackpanel

/**
 * Formatea milisegundos a tiempo visible del panel.
 */
internal fun formatPlaybackTime(ms: Long): String {
    val safeMs = ms.coerceAtLeast(0L)
    val totalSeconds = safeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)
}