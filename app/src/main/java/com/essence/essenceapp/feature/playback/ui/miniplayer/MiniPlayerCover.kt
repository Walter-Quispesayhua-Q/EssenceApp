package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

/**
 * Portada compacta del MiniPlayer.
 *
 * Muestra imagen real cuando existe, fallback cuando no hay imagen y una capa
 * animada cuando la cancion esta sonando.
 */
@Composable
internal fun MiniPlayerCover(
    title: String,
    imageUrl: String?,
    coverSize: Dp,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val glowAlpha = if (isActive) {
        val transition = rememberInfiniteTransition(label = "mini_cover_glow")
        transition.animateFloat(
            initialValue = 0.12f,
            targetValue = 0.32f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = MiniPlayerDefaults.PulseCycleMs,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mini_cover_glow_value"
        ).value
    } else {
        0f
    }

    Box(
        modifier = modifier.size(coverSize + 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(coverSize + 6.dp)
                    .clip(MiniPlayerDefaults.CoverGlowShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Surface(
            modifier = Modifier.size(coverSize),
            shape = MiniPlayerDefaults.CoverShape,
            color = GraphiteSurface,
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f)),
            shadowElevation = 6.dp
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                MiniPlayerFallbackCover(title = title)
            }
        }

        if (isActive) {
            Box(
                modifier = Modifier
                    .size(coverSize)
                    .clip(MiniPlayerDefaults.CoverShape)
                    .background(Color.Black.copy(alpha = 0.42f)),
                contentAlignment = Alignment.Center
            ) {
                MiniPlayerEqualizer()
            }
        }
    }
}

@Composable
private fun MiniPlayerFallbackCover(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SoftRose.copy(alpha = 0.28f),
                        MutedTeal.copy(alpha = 0.18f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite.copy(alpha = 0.44f)
        )
    }
}