package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun ArtistCoverPortrait(
    imageKey: String?,
    name: String,
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
            modifier = Modifier.size(208.dp),
            shape = CircleShape,
            color = GraphiteSurface.copy(alpha = 0.85f),
            border = BorderStroke(2.dp, MutedTeal.copy(alpha = 0.32f))
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                ArtistInitialPlaceholder(name = name)
            }
        }
    }
}

@Composable
private fun OuterRadialHalo() {
    Box(
        modifier = Modifier
            .size(248.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.28f),
                        SoftRose.copy(alpha = 0.10f),
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
            .size(224.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.18f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun ArtistInitialPlaceholder(name: String) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString().orEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.42f),
                        SoftRose.copy(alpha = 0.22f),
                        GraphiteSurface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (initial.isNotEmpty()) {
            Text(
                text = initial,
                style = MaterialTheme.typography.displayLarge,
                color = PureWhite.copy(alpha = 0.62f)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = LuxeGold.copy(alpha = 0.50f),
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
