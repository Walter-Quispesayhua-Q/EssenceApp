package com.essence.essenceapp.feature.history.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.history.ui.HistoryUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.playback.mapper.toQueueItems
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate

@Composable
fun HistoryContent(
    modifier: Modifier = Modifier,
    state: HistoryUiState,
    onRetry: () -> Unit,
    onOpenSong: (PlaybackOpenRequest) -> Unit
) {
    when (state) {
        HistoryUiState.Loading -> HistoryLoadingState(modifier)
        is HistoryUiState.Error -> AppErrorState(
            modifier = modifier,
            title = "No se pudo cargar el historial",
            message = state.message,
            onRetry = onRetry
        )
        is HistoryUiState.Success -> {
            if (state.songs.isEmpty()) {
                HistoryEmptyState(modifier)
            } else {
                HistorySuccessState(
                    modifier = modifier,
                    songs = state.songs,
                    onOpenSong = onOpenSong
                )
            }
        }
    }
}

@Composable
private fun HistorySuccessState(
    modifier: Modifier = Modifier,
    songs: List<SongSimple>,
    onOpenSong: (PlaybackOpenRequest) -> Unit
) {
    val bottomClearance = LocalBottomBarClearance.current
    val queueItems = remember(songs) { songs.toQueueItems() }
    val hero = songs.first()
    val rest = songs.drop(1)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = bottomClearance + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            HeroSongCard(
                song = hero,
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    onOpenSong(
                        PlaybackOpenRequest(
                            songLookup = hero.detailLookup,
                            queue = queueItems,
                            startIndex = 0,
                            sourceKey = "history"
                        )
                    )
                }
            )
        }

        if (rest.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Anteriores",
                    count = rest.size,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                SongsIsland(
                    songs = rest,
                    queueItems = queueItems,
                    queueOffset = 1,
                    onOpenSong = onOpenSong,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroSongCard(
    song: SongSimple,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val imageUrl = remember(song.imageKey) { resolveImageUrl(song.imageKey) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = GraphiteSurface,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.18f)),
        tonalElevation = 0.dp
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MutedTeal.copy(alpha = 0.10f),
                                SoftRose.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(18.dp),
                        color = MidnightBlackTransparentDark,
                        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f))
                    ) {
                        if (imageUrl != null) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = song.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(18.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                SoftRose.copy(alpha = 0.32f),
                                                MutedTeal.copy(alpha = 0.22f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = song.title.take(1).uppercase(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite.copy(alpha = 0.65f)
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(34.dp),
                        shape = CircleShape,
                        color = MutedTeal,
                        border = BorderStroke(2.dp, GraphiteSurface)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MutedTeal.copy(alpha = 0.14f),
                        border = BorderStroke(0.5.dp, MutedTeal.copy(alpha = 0.28f))
                    ) {
                        Text(
                            text = "Última escuchada",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MutedTeal,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = song.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite.copy(alpha = 0.62f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    song.albumName?.takeIf { it.isNotBlank() }?.let { album ->
                        Text(
                            text = album,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedTeal.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private val MidnightBlackTransparentDark = Color(0xFF181818)

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MutedTeal, MutedTeal.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SongsIsland(
    songs: List<SongSimple>,
    queueItems: List<PlaybackQueueItem>,
    queueOffset: Int,
    onOpenSong: (PlaybackOpenRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            songs.forEachIndexed { index, song ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOpenSong(
                                PlaybackOpenRequest(
                                    songLookup = song.detailLookup,
                                    queue = queueItems,
                                    startIndex = queueOffset + index,
                                    sourceKey = "history"
                                )
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    CompactSongContent(
                        song = song,
                        durationText = formatDuration(song.durationMs.toLong()),
                        showAddToPlaylistAction = true
                    )
                }
                if (index < songs.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryLoadingState(modifier: Modifier = Modifier) {
    val bottomClearance = LocalBottomBarClearance.current
    val shimmer = rememberShimmerAlpha()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = bottomClearance + 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerBlock(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.55f),
            alpha = shimmer
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.06f))
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(7) { index ->
                    SongRowSkeleton(alpha = shimmer)
                    if (index < 6) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SongRowSkeleton(alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBlock(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp)),
            alpha = alpha,
            cornerRadius = 14.dp
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .height(13.dp)
                    .fillMaxWidth(0.7f),
                alpha = alpha
            )
            ShimmerBlock(
                modifier = Modifier
                    .height(11.dp)
                    .fillMaxWidth(0.45f),
                alpha = alpha
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        ShimmerBlock(
            modifier = Modifier
                .height(11.dp)
                .width(34.dp),
            alpha = alpha
        )
    }
}

@Composable
private fun ShimmerBlock(
    modifier: Modifier = Modifier,
    alpha: Float,
    cornerRadius: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        GraphiteSurface.copy(alpha = 0.6f),
                        PureWhite.copy(alpha = 0.04f * alpha + 0.04f),
                        GraphiteSurface.copy(alpha = 0.6f)
                    )
                )
            )
    )
}

@Composable
private fun rememberShimmerAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "history-shimmer")
    val value by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "history-shimmer-alpha"
    )
    return value
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(132.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MutedTeal.copy(alpha = 0.18f),
                                SoftRose.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MutedTeal.copy(alpha = 0.22f),
                                MutedTeal.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = GraphiteSurface,
                    border = BorderStroke(0.5.dp, MutedTeal.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = MutedTeal,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu historial empieza aquí",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Reproduce una canción y verás cómo se construye tu viaje musical en este espacio.",
            style = MaterialTheme.typography.bodyMedium,
            color = PureWhite.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = MutedTeal.copy(alpha = 0.08f),
            border = BorderStroke(0.5.dp, MutedTeal.copy(alpha = 0.2f))
        ) {
            Text(
                text = "Se actualiza automáticamente",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MutedTeal.copy(alpha = 0.85f)
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private val previewSongs = listOf(
    SongSimple(1L, "Blinding Lights", 200_000, "", null, "single", 1_000_000L, "The Weeknd", "After Hours", LocalDate.of(2020, 3, 20)),
    SongSimple(2L, "Starboy", 230_000, "", null, "single", 900_000L, "The Weeknd", "Starboy", LocalDate.of(2016, 11, 25)),
    SongSimple(3L, "Save Your Tears", 215_000, "", null, "single", 850_000L, "The Weeknd", "After Hours", LocalDate.of(2020, 3, 20))
)

@Preview(name = "History - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(state = HistoryUiState.Loading, onRetry = {}, onOpenSong = {})
        }
    }
}

@Preview(name = "History - Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun EmptyPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(state = HistoryUiState.Success(songs = emptyList()), onRetry = {}, onOpenSong = {})
        }
    }
}

@Preview(name = "History - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(state = HistoryUiState.Success(songs = previewSongs), onRetry = {}, onOpenSong = {})
        }
    }
}