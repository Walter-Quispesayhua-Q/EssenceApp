package com.essence.essenceapp.shared.ui.components.cards.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun GridPlaylistContent(
    playlist: PlaylistSimple,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(132.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val artworkShape = RoundedCornerShape(22.dp)
        Surface(
            modifier = Modifier.size(104.dp),
            shape = artworkShape,
            color = GraphiteSurface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Box(modifier = Modifier.clip(artworkShape)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftRose.copy(alpha = 0.34f),
                                    LuxeGold.copy(alpha = 0.18f),
                                    MutedTeal.copy(alpha = 0.16f),
                                    GraphiteSurface
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Text(
                        text = playlist.title.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite.copy(alpha = 0.32f),
                        modifier = Modifier.align(Alignment.BottomStart)
                    )

                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd),
                        shape = RoundedCornerShape(999.dp),
                        color = GraphiteSurface.copy(alpha = 0.55f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistPlay,
                            contentDescription = null,
                            tint = PureWhite.copy(alpha = 0.75f),
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .size(14.dp)
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
                                    PureWhite.copy(alpha = 0.16f),
                                    PureWhite.copy(alpha = 0.04f)
                                )
                            ),
                            shape = artworkShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = PureWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GridPill(
                icon = if (playlist.isPublic) Icons.Default.Public else Icons.Outlined.Lock,
                text = if (playlist.isPublic) "Publica" else "Privada",
                accent = if (playlist.isPublic) MutedTeal else PureWhite.copy(alpha = 0.72f)
            )

            playlist.totalLikes?.takeIf { it > 0 }?.let { likes ->
                GridPill(
                    icon = Icons.Default.Favorite,
                    text = formatLikes(likes),
                    accent = SoftRose
                )
            }
        }
    }
}

@Composable
private fun GridPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.14f),
                            accent.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    width = 0.5.dp,
                    color = accent.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(9.dp),
                tint = accent.copy(alpha = 0.9f)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = accent.copy(alpha = 0.9f)
            )
        }
    }
}

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

@Preview(name = "Grid Playlist - Public", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PublicPreview() {
    EssenceAppTheme {
        BaseCard { GridPlaylistContent(playlist = previewPlaylist) }
    }
}

@Preview(name = "Grid Playlist - Private", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PrivatePreview() {
    EssenceAppTheme {
        BaseCard { GridPlaylistContent(playlist = previewPrivatePlaylist) }
    }
}