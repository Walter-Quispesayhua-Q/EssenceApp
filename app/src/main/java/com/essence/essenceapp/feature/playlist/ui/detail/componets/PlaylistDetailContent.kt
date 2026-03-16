package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.foundation.BorderStroke
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailAction
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ShortDateFormatter =
    DateTimeFormatter.ofPattern("dd MMM", Locale("es", "ES"))

@Composable
fun PlaylistDetailContent(
    modifier: Modifier = Modifier,
    state: PlaylistDetailUiState,
    onAction: (PlaylistDetailAction) -> Unit,
    onRetry: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    when (state) {
        PlaylistDetailUiState.Loading -> DetailLoadingState(modifier)
        is PlaylistDetailUiState.Error -> AppErrorState(
            modifier = modifier,
            message = state.message,
            title = "No se pudo cargar la playlist",
            onRetry = onRetry
        )
        is PlaylistDetailUiState.Success -> DetailSuccessState(
            modifier = modifier,
            playlist = state.playlist,
            songs = state.songs,
            isSongsLoading = state.isSongsLoading,
            isLikeSubmitting = state.isLikeSubmitting,
            onAction = onAction
        )
    }
}

@Composable
private fun DetailSuccessState(
    modifier: Modifier,
    playlist: Playlist,
    songs: List<SongSimple>,
    isSongsLoading: Boolean,
    isLikeSubmitting: Boolean,
    onAction: (PlaylistDetailAction) -> Unit
) {
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomClearance + 24.dp)
    ) {
        item {
            HeroSection(
                playlist = playlist,
                isLikeSubmitting = isLikeSubmitting,
                onToggleLike = { onAction(PlaylistDetailAction.ToggleLike) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            StatsIsland(
                playlist = playlist,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            ActionsRow(
                onEdit = { onAction(PlaylistDetailAction.EditPlaylist) },
                onDelete = { onAction(PlaylistDetailAction.DeletePlaylist) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SongsHeader(
                totalSongs = playlist.totalSongs,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            isSongsLoading -> item {
                SongsLoadingContent(modifier = Modifier.padding(horizontal = 16.dp))
            }
            songs.isEmpty() -> item {
                SongsEmptyContent(modifier = Modifier.padding(horizontal = 16.dp))
            }
            else -> item {
                SongsIsland(
                    songs = songs,
                    isPublic = playlist.isPublic,
                    onRemove = { songId ->
                        onAction(PlaylistDetailAction.RemoveSong(songId))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroSection(
    playlist: Playlist,
    isLikeSubmitting: Boolean,
    onToggleLike: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box {
                CoverArt()

                if (playlist.isPublic) {
                    LikeButton(
                        isLiked = playlist.isLiked,
                        isSubmitting = isLikeSubmitting,
                        onClick = onToggleLike,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                    )
                }
            }

            Text(
                text = playlist.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (!playlist.description.isNullOrBlank()) {
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StatsIsland(
    playlist: Playlist,
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
                icon = if (playlist.isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                value = if (playlist.isPublic) "Pública" else "Privada",
                label = "Visibilidad",
                tint = MutedTeal
            )

            CellDivider()

            StatCell(
                icon = Icons.Outlined.MusicNote,
                value = "${playlist.totalSongs}",
                label = "Canciones",
                tint = SoftRose
            )

            if (playlist.isPublic) {
                CellDivider()

                StatCell(
                    icon = Icons.Default.FavoriteBorder,
                    value = "${playlist.totalLikes ?: 0}",
                    label = "Likes",
                    tint = SoftRose
                )
            }

            CellDivider()

            StatCell(
                icon = Icons.Outlined.CalendarMonth,
                value = (playlist.updatedAt ?: playlist.createdAt).format(ShortDateFormatter),
                label = "Actualizada",
                tint = MutedTeal
            )
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
            modifier = Modifier.size(20.dp)
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
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedTeal)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Editar playlist",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
        ) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar playlist",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SongsHeader(
    totalSongs: Int,
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
            text = "Canciones",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$totalSongs",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SongsIsland(
    songs: List<SongSimple>,
    isPublic: Boolean,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            songs.forEachIndexed { index, song ->
                if (!isPublic && song.id != null) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                onRemove(song.id)
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    ),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Quitar",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
                    ) {
                        SongItemContent(song = song)
                    }
                } else {
                    SongItemContent(song = song)
                }

                if (index < songs.size - 1) {
                    IslandDivider()
                }
            }
        }
    }
}

@Composable
private fun SongItemContent(song: SongSimple) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        CompactSongContent(
            song = song,
            durationText = formatDuration(song.durationMs.toLong())
        )
    }
}

@Composable
private fun SongsLoadingContent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = SoftRose
            )
            Text(
                text = "Cargando canciones...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SongsEmptyContent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
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
                        imageVector = Icons.Outlined.QueueMusic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = "Sin canciones aún",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Agrega canciones para llenar tu playlist",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                textAlign = TextAlign.Center
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
private fun CoverArt() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier.size(176.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
            ),
            tonalElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = MutedTeal.copy(alpha = 0.6f),
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailLoadingState(modifier: Modifier = Modifier) {
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
                color = MutedTeal
            )
            Text(
                text = "Cargando...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun IslandDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    )
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private val previewPlaylist = Playlist(
    id = 1L,
    title = "Noches Lo-Fi",
    description = "Selección para estudiar y concentrarse con calma.",
    imageKey = null,
    isPublic = true,
    totalSongs = 3,
    createdAt = LocalDate.of(2025, 11, 20),
    updatedAt = LocalDate.of(2026, 3, 2),
    totalLikes = 42,
    isLiked = true
)

private val previewSongs = listOf(
    SongSimple(1L, "Tití Me Preguntó", 210_000, "", null, "single", 1_200_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(2L, "Ojitos Lindos", 245_000, "", null, "single", 900_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(3L, "Efecto", 198_000, "", null, "single", 750_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6))
)

@Preview(name = "Detail - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewPlaylist,
                songs = previewSongs,
                isSongsLoading = false,
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Detail - Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun EmptyPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewPlaylist.copy(totalSongs = 0, isPublic = false),
                songs = emptyList(),
                isSongsLoading = false,
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Detail - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(state = PlaylistDetailUiState.Loading, onAction = {})
    }
}