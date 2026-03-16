package com.essence.essenceapp.ui.shell.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.ui.manager.NowPlayingInfo
import com.essence.essenceapp.feature.song.ui.manager.PlaybackUiState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun MiniPlayer(
    nowPlaying: NowPlayingInfo,
    playback: PlaybackUiState,
    onTogglePlay: () -> Unit,
    onDismiss: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (playback.durationMs > 0)
        (playback.positionMs.toFloat() / playback.durationMs).coerceIn(0f, 1f)
    else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onTap),
        shape = RoundedCornerShape(22.dp),
        color = GraphiteSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f)),
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.08f),
                                Color.Transparent,
                                MutedTeal.copy(alpha = 0.04f)
                            )
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = GraphiteSurface,
                        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            SoftRose.copy(alpha = 0.25f),
                                            MutedTeal.copy(alpha = 0.15f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nowPlaying.title.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite.copy(alpha = 0.4f)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = nowPlaying.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = nowPlaying.artistName,
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = SoftRose,
                        shadowElevation = 4.dp,
                        onClick = onTogglePlay
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (playback.isPlaying)
                                    Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playback.isPlaying) "Pausar" else "Reproducir",
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = PureWhite.copy(alpha = 0.35f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .padding(horizontal = 14.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PureWhite.copy(alpha = 0.04f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(SoftRose, SoftRose.copy(alpha = 0.6f))
                                ),
                                RoundedCornerShape(999.dp)
                            )
                    )
                }

                Box(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniPlayerPreview() {
    EssenceAppTheme {
        MiniPlayer(
            nowPlaying = NowPlayingInfo(
                songLookup = "songs/titi",
                title = "Tití Me Preguntó",
                artistName = "Bad Bunny",
                imageKey = null
            ),
            playback = PlaybackUiState(
                isPlaying = true,
                positionMs = 75_000L,
                durationMs = 210_000L
            ),
            onTogglePlay = {},
            onDismiss = {},
            onTap = {},
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Preview(name = "Mini Player - Paused", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniPlayerPausedPreview() {
    EssenceAppTheme {
        MiniPlayer(
            nowPlaying = NowPlayingInfo(
                songLookup = "songs/moscow",
                title = "Moscow Mule",
                artistName = "Bad Bunny",
                imageKey = null
            ),
            playback = PlaybackUiState(
                isPlaying = false,
                positionMs = 30_000L,
                durationMs = 180_000L
            ),
            onTogglePlay = {},
            onDismiss = {},
            onTap = {},
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}