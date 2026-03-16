package com.essence.essenceapp.feature.song.ui.manager.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.ui.manager.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import kotlin.math.max

@Composable
fun PlaybackManagerContent(
    modifier: Modifier = Modifier,
    state: PlaybackUiState,
    onAction: (SongDetailManagerAction) -> Unit,
    songTitle: String? = null,
    artistName: String? = null,
    isLiked: Boolean = false,
    isLikeSubmitting: Boolean = false,
    onToggleLike: (() -> Unit)? = null
) {
    val durationMs = max(state.durationMs, 1L)
    val positionMs = state.positionMs.coerceIn(0L, durationMs)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (songTitle != null) {
            SongInfoIsland(
                title = songTitle,
                artist = artistName ?: "",
                isLiked = isLiked,
                isLikeSubmitting = isLikeSubmitting,
                onToggleLike = onToggleLike
            )
        }

        ProgressIsland(
            positionMs = positionMs,
            durationMs = durationMs,
            isBuffering = state.isBuffering,
            onSeek = { onAction(SongDetailManagerAction.SeekTo(it)) }
        )

        ControlsIsland(
            isPlaying = state.isPlaying,
            isRepeat = state.isRepeat,
            onAction = onAction
        )
    }
}

@Composable
private fun SongInfoIsland(
    title: String,
    artist: String,
    isLiked: Boolean,
    isLikeSubmitting: Boolean,
    onToggleLike: (() -> Unit)?
) {
    GlassIsland(accent = LuxeGold) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (artist.isNotBlank()) {
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (onToggleLike != null) {
                Spacer(modifier = Modifier.width(12.dp))
                LikeButton(
                    isLiked = isLiked,
                    isSubmitting = isLikeSubmitting,
                    onClick = onToggleLike
                )
            }
        }
    }
}

@Composable
private fun ProgressIsland(
    positionMs: Long,
    durationMs: Long,
    isBuffering: Boolean,
    onSeek: (Long) -> Unit
) {
    GlassIsland(accent = SoftRose) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (isBuffering) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            strokeWidth = 1.5.dp,
                            color = MutedTeal
                        )
                        Text(
                            text = "Buffering",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedTeal.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Slider(
                value = positionMs.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..durationMs.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = SoftRose,
                    activeTrackColor = SoftRose,
                    inactiveTrackColor = PureWhite.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(positionMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite.copy(alpha = 0.45f)
                )
                Text(
                    text = "-${formatTime(durationMs - positionMs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite.copy(alpha = 0.45f)
                )
            }
        }
    }
}

@Composable
private fun ControlsIsland(
    isPlaying: Boolean,
    isRepeat: Boolean,
    onAction: (SongDetailManagerAction) -> Unit
) {
    GlassIsland(accent = MutedTeal) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RepeatButton(
                    isRepeat = isRepeat,
                    onClick = { onAction(SongDetailManagerAction.ToggleRepeat) }
                )

                ControlButton(
                    icon = Icons.Default.SkipPrevious,
                    description = "Anterior",
                    onClick = { onAction(SongDetailManagerAction.Previous) }
                )

                PlayPauseButton(
                    isPlaying = isPlaying,
                    onClick = {
                        onAction(
                            if (isPlaying) SongDetailManagerAction.Pause
                            else SongDetailManagerAction.Play
                        )
                    }
                )

                ControlButton(
                    icon = Icons.Default.SkipNext,
                    description = "Siguiente",
                    onClick = { onAction(SongDetailManagerAction.Next) }
                )

                StopButton(
                    onClick = { onAction(SongDetailManagerAction.Stop) }
                )
            }
        }
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
        color = SoftRose,
        shadowElevation = 8.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause
                else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                tint = PureWhite,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = PureWhite.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = PureWhite.copy(alpha = 0.8f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun RepeatButton(
    isRepeat: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = if (isRepeat) MutedTeal.copy(alpha = 0.15f) else Color.Transparent,
        border = if (isRepeat) BorderStroke(1.dp, MutedTeal.copy(alpha = 0.3f))
        else BorderStroke(0.dp, Color.Transparent)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (isRepeat) Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = if (isRepeat) "Desactivar repetir" else "Repetir canción",
                tint = if (isRepeat) MutedTeal else PureWhite.copy(alpha = 0.35f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StopButton(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = Color.Transparent
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Detener",
                tint = PureWhite.copy(alpha = 0.35f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LikeButton(
    isLiked: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = PureWhite.copy(alpha = 0.06f),
        border = BorderStroke(
            1.dp,
            if (isLiked) SoftRose.copy(alpha = 0.3f)
            else PureWhite.copy(alpha = 0.08f)
        )
    ) {
        IconButton(onClick = onClick, enabled = !isSubmitting) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = SoftRose
                )
            } else {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Quitar like" else "Dar like",
                    tint = if (isLiked) SoftRose
                    else PureWhite.copy(alpha = 0.45f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun GlassIsland(
    modifier: Modifier = Modifier,
    accent: Color,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = GraphiteSurface.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Preview(name = "Playback - Full", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FullPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = true,
                positionMs = 125_000L,
                durationMs = 240_000L,
                isRepeat = false
            ),
            onAction = {},
            songTitle = "Tití Me Preguntó",
            artistName = "Bad Bunny",
            isLiked = true,
            onToggleLike = {}
        )
    }
}

@Preview(name = "Playback - Repeat On", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun RepeatPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = false,
                positionMs = 45_000L,
                durationMs = 210_000L,
                isRepeat = true
            ),
            onAction = {},
            songTitle = "Moscow Mule",
            artistName = "Bad Bunny",
            isLiked = false,
            onToggleLike = {}
        )
    }
}

@Preview(name = "Playback - No Song Info", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MinimalPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = true,
                isBuffering = true,
                positionMs = 80_000L,
                durationMs = 200_000L
            ),
            onAction = {}
        )
    }
}