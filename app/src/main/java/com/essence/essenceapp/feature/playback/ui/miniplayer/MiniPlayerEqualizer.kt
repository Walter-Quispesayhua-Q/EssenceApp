package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.SoftRose

/**
 * Pequena animacion visual sobre la portada cuando el audio esta sonando.
 */
@Composable
internal fun MiniPlayerEqualizer(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mini_equalizer")

    val bar1 = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mini_eq_1"
    )

    val bar2 = transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mini_eq_2"
    )

    val bar3 = transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mini_eq_3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.height(14.dp)
    ) {
        listOf(bar1, bar2, bar3).forEach { anim ->
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 14.dp * anim.value)
                    .background(SoftRose, RoundedCornerShape(1.dp))
            )
        }
    }
}