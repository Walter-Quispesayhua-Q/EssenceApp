package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

/**
 * Botones de transporte del MiniPlayer.
 *
 * Se mantienen separados para que el layout principal no mezcle reglas de
 * estado con detalles visuales de iconos y tamanos.
 */
@Composable
internal fun MiniPreviousButton(
    enabled: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    MiniTransportButton(
        icon = Icons.Default.SkipPrevious,
        description = "Anterior",
        enabled = enabled,
        onClick = onClick,
        buttonSize = buttonSize,
        iconSize = iconSize
    )
}

@Composable
internal fun MiniNextButton(
    enabled: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    MiniTransportButton(
        icon = Icons.Default.SkipNext,
        description = "Siguiente",
        enabled = enabled,
        onClick = onClick,
        buttonSize = buttonSize,
        iconSize = iconSize
    )
}

@Composable
internal fun MiniPlayPauseButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    val color = if (isBuffering) MutedTeal else SoftRose

    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = color,
        shadowElevation = 6.dp
    ) {
        IconButton(onClick = onClick) {
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    strokeWidth = 2.dp,
                    color = PureWhite
                )
            } else {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = PureWhite,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
private fun MiniTransportButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(buttonSize)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) {
                PureWhite.copy(alpha = 0.88f)
            } else {
                PureWhite.copy(alpha = 0.22f)
            },
            modifier = Modifier.size(iconSize)
        )
    }
}