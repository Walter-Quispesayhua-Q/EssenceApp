package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistSongsLoadingState(modifier: Modifier = Modifier) {
    GlassIsland(
        modifier = modifier,
        accent = SoftRose,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(4) { index ->
                ShimmerRow()
                if (index < 3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .height(0.5.dp)
                            .background(PureWhite.copy(alpha = 0.04f))
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerRow() {
    val transition = rememberInfiniteTransition(label = "songs_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    val baseColor = PureWhite.copy(alpha = alpha)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(width = 18.dp, height = 14.dp, base = baseColor)

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(baseColor)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBox(width = 160.dp, height = 12.dp, base = baseColor)
            ShimmerBox(width = 110.dp, height = 10.dp, base = baseColor)
        }

        Spacer(modifier = Modifier.width(10.dp))

        ShimmerBox(width = 30.dp, height = 10.dp, base = baseColor)
    }
}

@Composable
private fun ShimmerBox(
    width: Dp,
    height: Dp,
    base: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        base,
                        base.copy(alpha = base.alpha * 1.4f),
                        base
                    )
                )
            )
    )
}

@Composable
internal fun PlaylistSongsEmptyState(modifier: Modifier = Modifier) {
    EmptyShell(
        modifier = modifier,
        icon = Icons.Outlined.QueueMusic,
        iconBackground = PureWhite.copy(alpha = 0.04f),
        iconTint = PureWhite.copy(alpha = 0.36f),
        title = "Sin canciones aún",
        subtitle = "Usa el botón de agregar canciones para empezar a llenar esta playlist.",
        subtitleColor = PureWhite.copy(alpha = 0.50f),
        accent = SoftRose
    )
}

@Composable
internal fun PlaylistLikedSongsEmptyState(modifier: Modifier = Modifier) {
    EmptyShell(
        modifier = modifier,
        icon = Icons.Default.FavoriteBorder,
        iconBackground = SoftRose.copy(alpha = 0.10f),
        iconTint = SoftRose.copy(alpha = 0.55f),
        title = "Aún no tienes favoritas",
        subtitle = "Dale me gusta a tus canciones favoritas y aparecerán aquí.",
        subtitleColor = SoftRose.copy(alpha = 0.62f),
        accent = SoftRose
    )
}

@Composable
private fun EmptyShell(
    modifier: Modifier,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    subtitleColor: Color,
    accent: Color
) {
    GlassIsland(
        modifier = modifier,
        accent = accent,
        contentPadding = PaddingValues(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = iconBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
