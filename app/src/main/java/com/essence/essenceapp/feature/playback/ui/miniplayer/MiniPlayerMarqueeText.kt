package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Texto de una sola linea con desplazamiento cuando no entra en pantalla.
 *
 * Evita que titulos largos rompan el layout del MiniPlayer.
 */
@Composable
internal fun MiniPlayerMarqueeText(
    text: String,
    style: TextStyle,
    fontWeight: FontWeight,
    color: Color,
    modifier: Modifier = Modifier
) {
    var containerWidthPx by remember { mutableIntStateOf(0) }
    var textWidthPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val needsScroll = containerWidthPx in 1 until textWidthPx

    val translateX = if (needsScroll) {
        val gapPx = with(density) { MiniPlayerDefaults.MarqueeGap.toPx() }
        val travelPx = textWidthPx + gapPx
        val velocityPxPerSec = with(density) {
            MiniPlayerDefaults.MarqueeVelocityDpPerSec.dp.toPx()
        }
        val durationMs = ((travelPx / velocityPxPerSec) * 1000f)
            .toInt()
            .coerceAtLeast(4000)

        val transition = rememberInfiniteTransition(label = "mini_marquee")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = -travelPx,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMs,
                    easing = LinearEasing,
                    delayMillis = 1500
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "mini_marquee_offset"
        ).value
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { containerWidthPx = it.size.width }
    ) {
        Row(
            modifier = Modifier.graphicsLayer {
                translationX = translateX
            }
        ) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                color = color,
                maxLines = 1,
                overflow = if (needsScroll) TextOverflow.Visible else TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier.onGloballyPositioned {
                    textWidthPx = it.size.width
                }
            )

            if (needsScroll) {
                Spacer(
                    modifier = Modifier.size(
                        width = MiniPlayerDefaults.MarqueeGap,
                        height = 1.dp
                    )
                )

                Text(
                    text = text,
                    style = style,
                    fontWeight = fontWeight,
                    color = color,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}