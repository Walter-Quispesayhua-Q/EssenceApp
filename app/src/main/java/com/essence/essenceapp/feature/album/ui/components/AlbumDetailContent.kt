package com.essence.essenceapp.feature.album.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.album.ui.AlbumDetailAction
import com.essence.essenceapp.feature.album.ui.AlbumDetailUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate
import java.util.Locale

private val AccentColor = Color(0xFF00CED1)
private val HorizontalMargin = 20.dp
private val SectionGap = 32.dp
private val RowGap = 16.dp

@Composable
fun AlbumDetailContent(
    modifier: Modifier = Modifier,
    state: AlbumDetailUiState,
    onAction: (AlbumDetailAction) -> Unit
) {
    when (state) {
        AlbumDetailUiState.Loading -> LoadingState(modifier = modifier)

        is AlbumDetailUiState.Error -> AppErrorState(
            modifier = modifier,
            message = state.message,
            title = "No se pudo cargar el álbum",
            onRetry = { onAction(AlbumDetailAction.Refresh) }
        )

        is AlbumDetailUiState.Success -> SuccessState(
            modifier = modifier,
            album = state.album,
            isLikeSubmitting = state.isLikeSubmitting,
            onAction = onAction
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Cargando álbum...",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    album: Album,
    isLikeSubmitting: Boolean,
    onAction: (AlbumDetailAction) -> Unit
) {
    val songs = album.songs.orEmpty()
    val totalPlays = songs.sumOf { it.totalPlays ?: 0L }
    val totalDurationMs = songs.sumOf { it.durationMs.toLong() }
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomClearance + 24.dp),
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        item {
            AlbumHeroSection(
                album = album,
                songs = songs,
                totalDurationMs = totalDurationMs,
                isLikeSubmitting = isLikeSubmitting,
                onToggleLike = { onAction(AlbumDetailAction.ToggleLike) }
            )
        }

        item {
            StatsSection(
                totalPlays = totalPlays,
                songsCount = songs.size
            )
        }

        item {
            ActionsSection(
                enabled = songs.isNotEmpty(),
                onPlay = {
                    songs.firstOrNull()?.let { onAction(AlbumDetailAction.OpenSong(it.id)) }
                },
                onShuffle = {
                    songs.shuffled().firstOrNull()?.let { onAction(AlbumDetailAction.OpenSong(it.id)) }
                }
            )
        }

        item {
            TracklistSection(
                songs = songs,
                onOpenSong = { onAction(AlbumDetailAction.OpenSong(it)) }
            )
        }
    }
}

@Composable
private fun AlbumHeroSection(
    album: Album,
    songs: List<SongSimple>,
    totalDurationMs: Long,
    isLikeSubmitting: Boolean,
    onToggleLike: () -> Unit
) {
    val subtitle = when {
        songs.isNotEmpty() -> songs.first().artistName
        album.artists.isNotEmpty() -> "${album.artists.size} artistas"
        else -> "Álbum oficial"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .padding(horizontal = HorizontalMargin)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.40f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.58f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        LikeButton(
            isLiked = album.isLiked,
            isSubmitting = isLikeSubmitting,
            onClick = onToggleLike,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AlbumCoverPlaceholder()

            VerifiedBadge()

            Text(
                text = album.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.70f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetaPill(text = "${songs.size} tracks")
                MetaPill(text = formatTotalDuration(totalDurationMs))
                album.releaseDate?.let { MetaPill(text = it.year.toString()) }
            }

            album.description?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.74f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LikeButton(
    isLiked: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
    ) {
        IconButton(
            onClick = onClick,
            enabled = !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Quitar like" else "Dar like",
                    tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun AlbumCoverPlaceholder() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = AccentColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Album,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(88.dp)
        )
    }
}

@Composable
private fun VerifiedBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AccentColor.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AccentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "Álbum oficial",
            style = MaterialTheme.typography.labelMedium,
            color = AccentColor
        )
    }
}

@Composable
private fun MetaPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatsSection(
    totalPlays: Long,
    songsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "PLAYS",
            value = formatPlays(totalPlays)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "META",
            value = "$songsCount Songs"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    BaseCard(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActionsSection(
    enabled: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = onPlay,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = Color(0xFF121212)
            )
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("PLAY")
        }

        OutlinedButton(
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = onShuffle
        ) {
            Icon(imageVector = Icons.Default.Shuffle, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("SHUFFLE")
        }
    }
}

@Composable
private fun TracklistSection(
    songs: List<SongSimple>,
    onOpenSong: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        verticalArrangement = Arrangement.spacedBy(RowGap)
    ) {
        Text(
            text = "Tracks",
            style = MaterialTheme.typography.titleLarge
        )

        if (songs.isEmpty()) {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(
                    text = "No hay canciones disponibles.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            return
        }

        songs.forEachIndexed { index, song ->
            val isPlaying = index == 0
            val borderColor = if (isPlaying) AccentColor.copy(alpha = 0.55f) else Color.Transparent

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${index + 1}".padStart(2, '0'),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isPlaying) AccentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f),
                    modifier = Modifier.width(26.dp)
                )

                BaseCard(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                        .clickable { onOpenSong(song.id) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompactSongContent(
                            song = song,
                            modifier = Modifier.weight(1f),
                            durationText = formatDuration(song.durationMs)
                        )

                        IconButton(onClick = { onOpenSong(song.id) }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Abrir canción"
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(durationMs: Int): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatTotalDuration(totalMs: Long): String {
    val totalMinutes = totalMs / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "$hours h $minutes min" else "$minutes min"
}

private fun formatPlays(value: Long): String {
    return when {
        value >= 1_000_000_000L -> String.format(Locale.US, "%.1fB", value / 1_000_000_000f)
        value >= 1_000_000L -> String.format(Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000L -> String.format(Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}

private val previewSongs = listOf(
    SongSimple(
        id = 1L,
        title = "Tití Me Preguntó",
        durationMs = 210_000,
        hlsMasterKey = "songs/titi/master.m3u8",
        imageKey = null,
        songType = "single",
        totalPlays = 1_200_000L,
        artistName = "Bad Bunny",
        albumName = "Un Verano Sin Ti",
        releaseDate = LocalDate.of(2022, 5, 6)
    ),
    SongSimple(
        id = 2L,
        title = "Moscow Mule",
        durationMs = 245_000,
        hlsMasterKey = "songs/moscow/master.m3u8",
        imageKey = null,
        songType = "album",
        totalPlays = 980_000L,
        artistName = "Bad Bunny",
        albumName = "Un Verano Sin Ti",
        releaseDate = LocalDate.of(2022, 5, 6)
    )
)

private val previewAlbum = Album(
    id = 10L,
    title = "Un Verano Sin Ti",
    description = "Álbum con vibra veraniega y mezcla de ritmos latinos.",
    imageKey = null,
    releaseDate = LocalDate.of(2022, 5, 6),
    artists = emptyList(),
    songs = previewSongs,
    isLiked = true
)

@Preview(name = "Album Detail - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AlbumDetailContentLoadingPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Loading,
            onAction = {}
        )
    }
}

@Preview(name = "Album Detail - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AlbumDetailContentErrorPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Error("Sin conexión"),
            onAction = {}
        )
    }
}

@Preview(name = "Album Detail - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AlbumDetailContentSuccessPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Success(
                album = previewAlbum,
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}