package com.essence.essenceapp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.R
import com.essence.essenceapp.ui.theme.MidnightBlack
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val HOLD_DURATION_MS = 70L
private const val EXIT_DURATION_MS = 90
private const val LOGO_SIZE_DP = 200

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        delay(HOLD_DURATION_MS)
        coroutineScope {
            launch {
                scale.animateTo(
                    targetValue = 0.92f,
                    animationSpec = tween(EXIT_DURATION_MS, easing = FastOutSlowInEasing)
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(EXIT_DURATION_MS, easing = FastOutSlowInEasing)
                )
            }
        }
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBlack),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_essence),
            contentDescription = null,
            modifier = Modifier
                .size(LOGO_SIZE_DP.dp)
                .graphicsLayer {
                    this.alpha = alpha.value
                    scaleX = scale.value
                    scaleY = scale.value
                }
        )
    }
}
