package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.SongDetailAction
import com.essence.essenceapp.feature.song.ui.SongDetailUiState
import com.essence.essenceapp.feature.song.ui.manager.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import com.essence.essenceapp.feature.song.ui.manager.components.PlaybackManagerContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate

@Composable
fun SongDetailContent(
    modifier: Modifier = Modifier,
    state: SongDetailUiState,
    onAction: (SongDetailAction) -> Unit,
    onManagerAction: (SongDetailManagerAction) -> Unit
) {
    when (state) {
        SongDetailUiState.Loading -> LoadingState(modifier = modifier)
        is SongDetailUiState.Error -> AppErrorState(
            modifier = modifier,
            message = state.message,
            title = "No se pudo cargar la canción",
            onRetry = { onAction(SongDetailAction.Refresh) }
        )
        is SongDetailUiState.Success -> SuccessState(
            modifier = modifier,
            song = state.song,
            playback = state.playback,
            isLikeSubmitting = state.isLikeSubmitting,
            onAction = onAction,
            onManagerAction = onManagerAction
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.5.dp,
                color = SoftRose
            )
            Text(
                text = "Cargando canción...",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    song: Song,
    playback: PlaybackUiState,
    isLikeSubmitting: Boolean,
    onAction: (SongDetailAction) -> Unit,
    onManagerAction: (SongDetailManagerAction) -> Unit
) {
    val bottomClearance = LocalBottomBarClearance.current

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.22f),
                            MutedTeal.copy(alpha = 0.12f),
                            MidnightBlack.copy(alpha = 0.85f),
                            MidnightBlack
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        GlassBackButton(
            onClick = { onAction(SongDetailAction.Back) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
                .zIndex(10f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(
                    top = 72.dp,
                    bottom = bottomClearance + 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoverSection(song = song)

            Spacer(modifier = Modifier.height(28.dp))

            MetaRow(song = song)

            Spacer(modifier = Modifier.height(24.dp))

            PlaybackManagerContent(
                state = playback,
                onAction = onManagerAction,
                songTitle = song.title,
                artistName = song.artists.joinToString(", ") { it.nameArtist },
                isLiked = song.isLiked,
                isLikeSubmitting = isLikeSubmitting,
                onToggleLike = { onAction(SongDetailAction.ToggleLike) }
            )

            Spacer(modifier = Modifier.height(22.dp))

            DetailsIsland(
                song = song,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun GlassBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = PureWhite.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CoverSection(song: Song) {
    Surface(
        modifier = Modifier.size(260.dp),
        shape = RoundedCornerShape(32.dp),
        color = GraphiteSurface,
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f)),
        shadowElevation = 24.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.3f),
                            MutedTeal.copy(alpha = 0.2f),
                            GraphiteSurface
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = song.title.take(2).uppercase(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = PureWhite.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun MetaRow(song: Song) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        song.songType?.takeIf { it.isNotBlank() }?.let {
            MetaPill(text = it, accent = MutedTeal)
        }
        song.releaseDate?.let {
            MetaPill(text = "${it.year}", accent = SoftRose)
        }
        song.totalPlays?.let {
            MetaPill(text = formatPlays(it), accent = LuxeGold)
        }
    }
}

@Composable
private fun DetailsIsland(
    song: Song,
    onAction: (SongDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassIsland(
        modifier = modifier,
        accent = MutedTeal,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (song.album != null) {
                RelationRow(
                    icon = Icons.Default.Album,
                    label = "Álbum",
                    value = song.album.title,
                    accent = LuxeGold,
                    onClick = { onAction(SongDetailAction.OpenAlbum(song.album.detailLookup)) }
                )
            }

            song.artists.forEach { artist ->
                RelationRow(
                    icon = Icons.Default.Person,
                    label = "Artista",
                    value = artist.nameArtist,
                    accent = MutedTeal,
                    onClick = { onAction(SongDetailAction.OpenArtist(artist.detailLookup)) }
                )
            }

            RelationRow(
                icon = Icons.Default.PlayArrow,
                label = "Duración",
                value = formatDuration(song.durationMs.toLong()),
                accent = SoftRose,
                onClick = null
            )
        }
    }
}

@Composable
private fun RelationRow(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(12.dp),
            color = accent.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetaPill(
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.12f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = accent.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun GlassIsland(
    modifier: Modifier = Modifier,
    accent: Color,
    contentPadding: PaddingValues = PaddingValues(20.dp),
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

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatPlays(value: Int): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}

private val previewSong = Song(
    id = 1L,
    title = "Tití Me Preguntó",
    durationMs = 210_000,
    hlsMasterKey = "songs/titi/master.m3u8",
    imageKey = null,
    songType = "single",
    totalPlays = 1_200_000,
    isLiked = true,
    artists = listOf(
        ArtistSimple(10L, "Bad Bunny", null, "artists/bad-bunny"),
        ArtistSimple(11L, "Chencho Corleone", null, "artists/chencho")
    ),
    album = AlbumSimple(20L, "Un Verano Sin Ti", null, "albums/uvst", listOf("Bad Bunny"), LocalDate.of(2022, 5, 6)),
    releaseDate = LocalDate.of(2022, 5, 6)
)

@Preview(name = "Song - Playing", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlayingPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Success(
                song = previewSong,
                playback = PlaybackUiState(isPlaying = true, positionMs = 72_000L, durationMs = 210_000L),
                isLikeSubmitting = false
            ),
            onAction = {},
            onManagerAction = {}
        )
    }
}

@Preview(name = "Song - Paused", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PausedPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Success(
                song = previewSong,
                playback = PlaybackUiState(isPlaying = false, positionMs = 45_000L, durationMs = 210_000L),
                isLikeSubmitting = false
            ),
            onAction = {},
            onManagerAction = {}
        )
    }
}