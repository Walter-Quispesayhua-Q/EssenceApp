package com.essence.essenceapp.shared.ui.components.cards.song

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.playlist.AddToPlaylistSheet
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate

private val CoverShape: Shape = RoundedCornerShape(16.dp)
private val PillShape: Shape = RoundedCornerShape(999.dp)

private val FallbackCoverBrush: Brush = Brush.linearGradient(
    colors = listOf(
        SoftRose.copy(alpha = 0.32f),
        MutedTeal.copy(alpha = 0.22f)
    )
)

private val SubtleBorderBrush: Brush = Brush.verticalGradient(
    colors = listOf(
        PureWhite.copy(alpha = 0.16f),
        PureWhite.copy(alpha = 0.04f)
    )
)

private val PlaysPillBrush: Brush = Brush.horizontalGradient(
    colors = listOf(
        LuxeGold.copy(alpha = 0.14f),
        LuxeGold.copy(alpha = 0.06f)
    )
)

private val AddBtnRadialBrush: Brush = Brush.radialGradient(
    colors = listOf(
        MutedTeal.copy(alpha = 0.12f),
        Color.Transparent
    )
)

@Composable
fun CompactSongContent(
    song: SongSimple,
    modifier: Modifier = Modifier,
    durationText: String? = null,
    showAddToPlaylistAction: Boolean = true,
    isPlaying: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val imageUrl = resolveImageUrl(song.imageKey)
    val durationLabel = durationText ?: formatDuration(song.durationMs.toLong())
    var showPlaylistSheet by rememberSaveable(song.id) { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SongCover(
            imageUrl = imageUrl,
            fallbackLetter = song.title.take(1).uppercase(),
            isPlaying = isPlaying
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            ArtistAlbumLine(
                artistName = song.artistName,
                albumName = song.albumName
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = durationLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = PureWhite.copy(alpha = 0.55f)
            )

            song.totalPlays?.let { plays ->
                PlaysPill(text = formatPlays(plays))
            }
        }

        if (showAddToPlaylistAction && song.hlsMasterKey.isNotBlank()) {
            Spacer(modifier = Modifier.width(10.dp))
            AddToPlaylistButton(onClick = { showPlaylistSheet = true })
        }

        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(10.dp))
            trailingContent()
        }
    }

    if (showAddToPlaylistAction && showPlaylistSheet && song.hlsMasterKey.isNotBlank()) {
        AddToPlaylistSheet(
            songKey = song.hlsMasterKey,
            onDismiss = { showPlaylistSheet = false }
        )
    }
}

@Composable
private fun SongCover(
    imageUrl: String?,
    fallbackLetter: String,
    isPlaying: Boolean
) {
    Box(modifier = Modifier.size(52.dp)) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CoverShape,
            color = GraphiteSurface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Box(modifier = Modifier.clip(CoverShape)) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = FallbackCoverBrush),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = fallbackLetter,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite.copy(alpha = 0.55f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            brush = SubtleBorderBrush,
                            shape = CoverShape
                        )
                )
            }
        }

        if (isPlaying) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
                    .size(18.dp),
                shape = CircleShape,
                color = MutedTeal.copy(alpha = 0.92f),
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistAlbumLine(
    artistName: String,
    albumName: String?
) {
    val showAlbum = !albumName.isNullOrBlank()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = artistName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.65f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (showAlbum) Modifier.weight(1f, fill = false) else Modifier
        )

        if (showAlbum) {
            Text(
                text = " · ",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.4f)
            )

            Text(
                text = albumName!!,
                style = MaterialTheme.typography.bodySmall,
                color = MutedTeal.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Composable
private fun PlaysPill(text: String) {
    Surface(
        shape = PillShape,
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(brush = PlaysPillBrush)
                .border(
                    width = 0.5.dp,
                    color = LuxeGold.copy(alpha = 0.22f),
                    shape = PillShape
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = LuxeGold.copy(alpha = 0.9f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AddToPlaylistButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.55f),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.clip(CircleShape)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = AddBtnRadialBrush)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        brush = SubtleBorderBrush,
                        shape = CircleShape
                    )
            )

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = "Agregar a playlist",
                    tint = MutedTeal,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatPlays(value: Long): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}

private val previewSong = SongSimple(
    id = 1L,
    title = "Titi Me Pregunto",
    durationMs = 210_000,
    hlsMasterKey = "songs/titi/master.m3u8",
    imageKey = null,
    songType = "single",
    totalPlays = 1_200_000L,
    artistName = "Bad Bunny",
    albumName = "Un Verano Sin Ti",
    releaseDate = LocalDate.of(2022, 5, 6)
)

private val previewSongMinimal = SongSimple(
    id = 2L,
    title = "Moscow Mule",
    durationMs = 245_000,
    hlsMasterKey = "songs/moscow/master.m3u8",
    imageKey = null,
    songType = null,
    totalPlays = null,
    artistName = "Bad Bunny",
    albumName = null,
    releaseDate = null
)

@Preview(name = "Compact Song - Full", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FullPreview() {
    EssenceAppTheme {
        BaseCard {
            CompactSongContent(
                song = previewSong,
                showAddToPlaylistAction = true
            )
        }
    }
}

@Preview(name = "Compact Song - Minimal", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MinimalPreview() {
    EssenceAppTheme {
        BaseCard {
            CompactSongContent(song = previewSongMinimal)
        }
    }
}

@Preview(name = "Compact Song - Playing", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlayingPreview() {
    EssenceAppTheme {
        BaseCard {
            CompactSongContent(
                song = previewSong,
                isPlaying = true
            )
        }
    }
}