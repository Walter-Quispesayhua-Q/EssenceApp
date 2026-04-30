package com.essence.essenceapp.shared.ui.components.cards.playlist

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.PlaylistUtils
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun CompactPlaylistContent(
    playlist: PlaylistSimple,
    modifier: Modifier = Modifier
) {
    val visual = remember(playlist.id, playlist.type, playlist.isPublic) {
        playlistVisual(playlist)
    }
    val displayTitle = remember(playlist.type, playlist.title) {
        PlaylistUtils.getDisplayTitle(playlist.type, playlist.title)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaylistArtwork(
            displayTitle = displayTitle,
            visual = visual,
            isLiked = visual.isLiked
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            PlaylistMetaRow(
                playlist = playlist,
                accent = visual.accent
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = visual.accent.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PlaylistArtwork(
    displayTitle: String,
    visual: PlaylistVisual,
    isLiked: Boolean
) {
    val artworkShape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier.size(56.dp),
        shape = artworkShape,
        color = Color.Transparent,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.clip(artworkShape)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(visual.gradient)),
                contentAlignment = Alignment.Center
            ) {
                if (isLiked) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = SoftRose,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Text(
                        text = displayTitle.firstNonBlankChar()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = PureWhite.copy(alpha = 0.85f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.18f),
                                visual.accent.copy(alpha = 0.22f)
                            )
                        ),
                        shape = artworkShape
                    )
            )
        }
    }
}

@Composable
private fun PlaylistMetaRow(
    playlist: PlaylistSimple,
    accent: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (playlist.isPublic) Icons.Default.Public else Icons.Outlined.Lock,
            contentDescription = null,
            modifier = Modifier.size(11.dp),
            tint = PureWhite.copy(alpha = 0.45f)
        )
        Text(
            text = if (playlist.isPublic) "Pública" else "Privada",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.55f),
            maxLines = 1
        )

        playlist.totalLikes?.takeIf { it > 0 }?.let { likes ->
            BulletDot(color = PureWhite.copy(alpha = 0.25f))
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(11.dp),
                tint = SoftRose.copy(alpha = 0.75f)
            )
            Text(
                text = formatLikes(likes),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = SoftRose.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BulletDot(color: Color) {
    Box(
        modifier = Modifier
            .size(3.dp)
            .background(color = color, shape = RoundedCornerShape(50))
    )
}

private data class PlaylistVisual(
    val gradient: List<Color>,
    val accent: Color,
    val isLiked: Boolean
)

private fun playlistVisual(playlist: PlaylistSimple): PlaylistVisual {
    if (PlaylistUtils.isSystemPlaylist(playlist.type)) {
        return PlaylistVisual(
            gradient = listOf(
                SoftRose.copy(alpha = 0.55f),
                SoftRose.copy(alpha = 0.22f),
                Color(0xFFBB4477).copy(alpha = 0.18f)
            ),
            accent = SoftRose,
            isLiked = true
        )
    }

    val hue = ((playlist.id * 47L + playlist.title.hashCode().toLong()).mod(360L)).toFloat()
    val accent = Color.hsl(hue, 0.55f, 0.55f)
    val gradient = listOf(
        Color.hsl(hue, 0.55f, 0.55f, alpha = 0.55f),
        Color.hsl((hue + 25f) % 360f, 0.45f, 0.45f, alpha = 0.30f),
        Color.hsl((hue + 50f) % 360f, 0.35f, 0.35f, alpha = 0.18f)
    )
    return PlaylistVisual(gradient = gradient, accent = accent, isLiked = false)
}

private fun String.firstNonBlankChar(): Char? = trim().firstOrNull { !it.isWhitespace() }

private fun formatLikes(value: Long): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}

private val previewPlaylist = PlaylistSimple(
    id = 1L,
    title = "Mis Favoritas 2024",
    isPublic = true,
    totalLikes = 42
)

private val previewPrivatePlaylist = PlaylistSimple(
    id = 2L,
    title = "Para el gym",
    isPublic = false,
    totalLikes = null
)

private val previewPopularPlaylist = PlaylistSimple(
    id = 3L,
    title = "Top Reggaeton",
    isPublic = true,
    totalLikes = 15_800
)

private val previewLikedPlaylist = PlaylistSimple(
    id = 0L,
    title = "Liked Songs",
    isPublic = false,
    type = "LIKED"
)

@Preview(name = "Compact Playlist - Public", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PublicPreview() {
    EssenceAppTheme {
        BaseCard { CompactPlaylistContent(playlist = previewPlaylist) }
    }
}

@Preview(name = "Compact Playlist - Private", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PrivatePreview() {
    EssenceAppTheme {
        BaseCard { CompactPlaylistContent(playlist = previewPrivatePlaylist) }
    }
}

@Preview(name = "Compact Playlist - Popular", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PopularPreview() {
    EssenceAppTheme {
        BaseCard { CompactPlaylistContent(playlist = previewPopularPlaylist) }
    }
}

@Preview(name = "Compact Playlist - Liked", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LikedPreview() {
    EssenceAppTheme {
        BaseCard { CompactPlaylistContent(playlist = previewLikedPlaylist) }
    }
}
