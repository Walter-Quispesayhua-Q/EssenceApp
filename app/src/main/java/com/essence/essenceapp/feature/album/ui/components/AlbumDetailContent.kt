package com.essence.essenceapp.feature.album.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.album.ui.AlbumDetailAction
import com.essence.essenceapp.feature.album.ui.AlbumDetailUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate
import java.util.Locale

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
            title = "No se pudo cargar el album",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = SoftRose
            )
            Text(
                text = "Cargando album...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
        contentPadding = PaddingValues(bottom = bottomClearance + 24.dp)
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
            Spacer(modifier = Modifier.height(16.dp))
            StatsIsland(
                totalPlays = totalPlays,
                songsCount = songs.size,
                totalDurationMs = totalDurationMs,
                releaseYear = album.releaseDate?.year,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            ActionsRow(
                enabled = songs.isNotEmpty(),
                onPlay = {
                    songs.firstOrNull()?.let { onAction(AlbumDetailAction.OpenSong(it.detailLookup)) }
                },
                onShuffle = {
                    songs.shuffled().firstOrNull()?.let { onAction(AlbumDetailAction.OpenSong(it.detailLookup)) }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            TracksHeader(
                count = songs.size,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (songs.isEmpty()) {
            item {
                TracksEmptyContent(modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            item {
                TracksIsland(
                    songs = songs,
                    onOpenSong = { onAction(AlbumDetailAction.OpenSong(it)) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
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
    val artistText = when {
        songs.isNotEmpty() -> songs.first().artistName
        album.artists.isNotEmpty() -> album.artists.joinToString(", ") { it.nameArtist }
        else -> "Album oficial"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SoftRose.copy(alpha = 0.25f),
                            MutedTeal.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
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
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VerifiedBadge()

            Text(
                text = album.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = artistText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )

            album.description?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StatsIsland(
    totalPlays: Long,
    songsCount: Int,
    totalDurationMs: Long,
    releaseYear: Int?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCell(
                icon = Icons.Outlined.MusicNote,
                value = "$songsCount",
                label = "Tracks",
                tint = SoftRose
            )

            CellDivider()

            StatCell(
                icon = Icons.Default.PlayArrow,
                value = formatPlays(totalPlays),
                label = "Plays",
                tint = MutedTeal
            )

            CellDivider()

            StatCell(
                icon = Icons.Default.Shuffle,
                value = formatTotalDuration(totalDurationMs),
                label = "Duración",
                tint = SoftRose
            )

            if (releaseYear != null) {
                CellDivider()

                StatCell(
                    icon = Icons.Default.CheckCircle,
                    value = "$releaseYear",
                    label = "Año",
                    tint = MutedTeal
                )
            }
        }
    }
}

@Composable
private fun StatCell(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun CellDivider() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(36.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    )
}

@Composable
private fun ActionsRow(
    enabled: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onPlay,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                1.dp,
                if (enabled) SoftRose.copy(alpha = 0.5f)
                else SoftRose.copy(alpha = 0.2f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SoftRose,
                disabledContentColor = SoftRose.copy(alpha = 0.3f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Reproducir",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onShuffle,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                1.dp,
                if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Aleatorio",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TracksHeader(
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
                        colors = listOf(SoftRose, SoftRose.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Tracks",
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
private fun TracksIsland(
    songs: List<SongSimple>,
    onOpenSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            songs.forEachIndexed { index, song ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSong(song.detailLookup) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${index + 1}".padStart(2, '0'),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 0) SoftRose
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        modifier = Modifier.width(24.dp)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        CompactSongContent(
                            song = song,
                            durationText = formatDuration(song.durationMs)
                        )
                    }
                }

                if (index < songs.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 50.dp, end = 14.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TracksEmptyContent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = "Sin canciones disponibles",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
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
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(
            1.dp,
            if (isLiked) SoftRose.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    ) {
        IconButton(onClick = onClick, enabled = !isSubmitting) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = SoftRose
                )
            } else {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Quitar like" else "Dar like",
                    tint = if (isLiked) SoftRose
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun VerifiedBadge() {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MutedTeal.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MutedTeal,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Album oficial",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MutedTeal
            )
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
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
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
    SongSimple(1L, "Titi Me Pregunto", 210_000, "songs/titi/master.m3u8", null, "single", 1_200_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(2L, "Moscow Mule", 245_000, "songs/moscow/master.m3u8", null, "album", 980_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(3L, "Ojitos Lindos", 224_000, "songs/ojitos/master.m3u8", null, "album", 870_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6))
)

private val previewAlbum = Album(
    id = 10L,
    title = "Un Verano Sin Ti",
    description = "Album con vibra veraniega y mezcla de ritmos latinos.",
    imageKey = null,
    releaseDate = LocalDate.of(2022, 5, 6),
    artists = emptyList(),
    songs = previewSongs,
    isLiked = true
)

@Preview(name = "Album - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        AlbumDetailContent(state = AlbumDetailUiState.Loading, onAction = {})
    }
}

@Preview(name = "Album - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Success(album = previewAlbum, isLikeSubmitting = false),
            onAction = {}
        )
    }
}

@Preview(name = "Album - Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun EmptyPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Success(
                album = previewAlbum.copy(songs = emptyList()),
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}