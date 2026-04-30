package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistCoverArt(
    playlist: Playlist,
    isSystem: Boolean,
    modifier: Modifier = Modifier
) {
    if (isSystem) {
        LikedCoverArt(modifier = modifier)
    } else {
        NormalCoverArt(playlist = playlist, modifier = modifier)
    }
}

@Composable
private fun LikedCoverArt(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(256.dp),
        contentAlignment = Alignment.Center
    ) {
        OuterHalo(
            colors = listOf(
                SoftRose.copy(alpha = 0.32f),
                SoftRose.copy(alpha = 0.14f),
                Color(0xFFBB4477).copy(alpha = 0.06f),
                Color.Transparent
            )
        )

        InnerHalo(
            sizeDp = 232,
            cornerDp = 34,
            colors = listOf(
                SoftRose.copy(alpha = 0.20f),
                Color.Transparent
            )
        )

        Surface(
            modifier = Modifier.size(216.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.22f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.40f),
                                Color(0xFFBB4477).copy(alpha = 0.28f),
                                GraphiteSurface.copy(alpha = 0.92f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = SoftRose.copy(alpha = 0.62f),
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
private fun NormalCoverArt(
    playlist: Playlist,
    modifier: Modifier = Modifier
) {
    val imageUrl = resolveImageUrl(playlist.imageKey)

    Box(
        modifier = modifier.size(256.dp),
        contentAlignment = Alignment.Center
    ) {
        OuterHalo(
            colors = listOf(
                MutedTeal.copy(alpha = 0.22f),
                SoftRose.copy(alpha = 0.10f),
                Color.Transparent
            )
        )

        InnerHalo(
            sizeDp = 232,
            cornerDp = 34,
            colors = listOf(
                MutedTeal.copy(alpha = 0.14f),
                Color.Transparent
            )
        )

        Surface(
            modifier = Modifier.size(216.dp),
            shape = RoundedCornerShape(32.dp),
            color = GraphiteSurface.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = playlist.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MutedTeal.copy(alpha = 0.34f),
                                    SoftRose.copy(alpha = 0.24f),
                                    GraphiteSurface
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistPlay,
                        contentDescription = null,
                        tint = PureWhite.copy(alpha = 0.28f),
                        modifier = Modifier.size(72.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OuterHalo(colors: List<Color>) {
    Box(
        modifier = Modifier
            .size(256.dp)
            .clip(RoundedCornerShape(38.dp))
            .background(brush = Brush.radialGradient(colors = colors))
    )
}

@Composable
private fun InnerHalo(
    sizeDp: Int,
    cornerDp: Int,
    colors: List<Color>
) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(cornerDp.dp))
            .background(brush = Brush.radialGradient(colors = colors))
    )
}
