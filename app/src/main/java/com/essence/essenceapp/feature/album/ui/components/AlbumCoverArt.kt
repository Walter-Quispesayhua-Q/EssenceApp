package com.essence.essenceapp.feature.album.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
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
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun AlbumCoverArt(
    imageKey: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    val imageUrl = resolveImageUrl(imageKey)

    Box(
        modifier = modifier.size(248.dp),
        contentAlignment = Alignment.Center
    ) {
        OuterRadialHalo()
        InnerSoftHalo()

        Surface(
            modifier = Modifier.size(212.dp),
            shape = RoundedCornerShape(20.dp),
            color = GraphiteSurface.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.10f))
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AlbumCoverPlaceholder()
            }
        }
    }
}

@Composable
private fun OuterRadialHalo() {
    Box(
        modifier = Modifier
            .size(248.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SoftRose.copy(alpha = 0.20f),
                        MutedTeal.copy(alpha = 0.12f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun InnerSoftHalo() {
    Box(
        modifier = Modifier
            .size(228.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.14f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun AlbumCoverPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.36f),
                        SoftRose.copy(alpha = 0.22f),
                        GraphiteSurface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Album,
            contentDescription = null,
            tint = PureWhite.copy(alpha = 0.32f),
            modifier = Modifier.size(72.dp)
        )
    }
}
