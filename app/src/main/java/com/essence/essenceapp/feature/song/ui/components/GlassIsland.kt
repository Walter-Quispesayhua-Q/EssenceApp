package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.PureWhite

private val IslandShape = RoundedCornerShape(28.dp)

@Composable
fun GlassIsland(
    modifier: Modifier = Modifier,
    accent: Color,
    isPulsing: Boolean = false,
    accentAlpha: Float = 0.07f,
    materialAlpha: Float = 0.62f,
    showInnerShadow: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_anim"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPulsing) pulse else 1.0f,
        animationSpec = tween(300),
        label = "scale_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(IslandShape)
            .background(GraphiteSurface.copy(alpha = materialAlpha))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to PureWhite.copy(alpha = 0.14f),
                        0.30f to PureWhite.copy(alpha = 0.04f),
                        0.60f to Color.Transparent,
                        1.0f to Color.Transparent
                    )
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = accentAlpha),
                            accent.copy(alpha = accentAlpha * 0.30f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.0f),
                        radius = 720f
                    )
                )
        )

        if (showInnerShadow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.65f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.14f)
                        )
                    )
            )
        }

        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
