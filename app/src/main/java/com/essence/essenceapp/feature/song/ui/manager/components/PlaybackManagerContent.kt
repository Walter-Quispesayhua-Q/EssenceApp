package com.essence.essenceapp.feature.song.ui.manager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.ui.manager.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import kotlin.math.max

@Composable
fun PlaybackManagerContent(
    modifier: Modifier = Modifier,
    state: PlaybackUiState,
    onAction: (SongDetailManagerAction) -> Unit
) {
    val durationMs = max(state.durationMs, 1L)
    val positionMs = state.positionMs.coerceIn(0L, durationMs)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Control de reproducción",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Slider(
            value = positionMs.toFloat(),
            onValueChange = { onAction(SongDetailManagerAction.SeekTo(it.toLong())) },
            valueRange = 0f..durationMs.toFloat()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(positionMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onAction(SongDetailManagerAction.Previous) }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Anterior"
                )
            }

            Button(
                onClick = {
                    onAction(
                        if (state.isPlaying) SongDetailManagerAction.Pause
                        else SongDetailManagerAction.Play
                    )
                }
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pausar" else "Reproducir"
                )
            }

            IconButton(onClick = { onAction(SongDetailManagerAction.Next) }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Siguiente"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onAction(SongDetailManagerAction.SeekBy(-10_000L)) }
            ) {
                Text("-10s")
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onAction(SongDetailManagerAction.Stop) }
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Detener"
                )
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onAction(SongDetailManagerAction.SeekBy(10_000L)) }
            ) {
                Text("+10s")
            }
        }

        if (state.isBuffering) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(strokeWidth = 2.dp)
                Text(
                    text = "Buffering...",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Preview(name = "Playback - Paused", showBackground = true)
@Composable
private fun PlaybackManagerPausedPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = false,
                positionMs = 45_000L,
                durationMs = 210_000L
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Playback - Playing", showBackground = true)
@Composable
private fun PlaybackManagerPlayingPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = true,
                isBuffering = false,
                positionMs = 125_000L,
                durationMs = 240_000L
            ),
            onAction = {}
        )
    }
}
