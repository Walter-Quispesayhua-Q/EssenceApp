package com.essence.essenceapp.feature.song.ui.components.playbackpanel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.ui.components.GlassIsland

/**
 * Superficie visual del panel de reproduccion dentro de SongDetail.
 *
 * Mantiene el estilo glass de la pantalla de cancion sin llevar esa dependencia
 * al modulo global de playback.
 */
@Composable
internal fun PlaybackPanelSurface(
    isPlaying: Boolean,
    isBuffering: Boolean,
    colors: PlaybackPanelColors,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassIsland(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        accent = colors.accent,
        isPulsing = isPlaying && !isBuffering,
        accentAlpha = colors.accentStrength,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        content()
    }
}