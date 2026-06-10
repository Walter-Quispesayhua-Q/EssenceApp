package com.essence.essenceapp.feature.song.ui.components.playbackpanel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

/**
 * Cabecera compacta del panel.
 *
 * Muestra el estado visual de reproduccion y, si existen, los metadatos de la
 * cancion que SongDetail ya tiene a mano.
 */
@Composable
internal fun PlaybackMetaHeader(
    songTitle: String?,
    artistName: String?,
    isBuffering: Boolean,
    isPlaying: Boolean,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = accent.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.32f))
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Reproduciendo",
                tint = accent,
                modifier = Modifier
                    .padding(7.dp)
                    .size(18.dp)
            )
        }

        if (!songTitle.isNullOrBlank() || !artistName.isNullOrBlank()) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                songTitle?.takeIf { it.isNotBlank() }?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                artistName?.takeIf { it.isNotBlank() }?.let { artist ->
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite.copy(alpha = 0.52f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        PlaybackStateIndicator(
            isBuffering = isBuffering,
            isPlaying = isPlaying
        )
    }
}

@Composable
private fun PlaybackStateIndicator(
    isBuffering: Boolean,
    isPlaying: Boolean
) {
    if (!isBuffering && !isPlaying) return

    if (isBuffering) {
        CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            strokeWidth = 1.6.dp,
            color = MutedTeal
        )
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = SoftRose,
                    shape = CircleShape
                )
        )
    }
}