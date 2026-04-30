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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistDetailLoading(modifier: Modifier = Modifier) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val transition = rememberInfiniteTransition(label = "detail_shimmer")
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.18f),
                            SoftRose.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = statusBarTopPadding + 12.dp,
                    bottom = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ShimmerBox(width = 44.dp, height = 44.dp, base = baseColor, shape = CircleShape)
            CoverPlaceholder(base = baseColor)
            TitleBlock(base = baseColor)
            ActionsRowPlaceholder(base = baseColor)
            StatsPlaceholder(base = baseColor)
            SongsListPlaceholder(base = baseColor)
        }
    }
}

@Composable
private fun CoverPlaceholder(base: Color) {
    Box(
        modifier = Modifier
            .size(216.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(base)
    )
}

@Composable
private fun TitleBlock(base: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ShimmerBox(width = 110.dp, height = 22.dp, base = base, shape = RoundedCornerShape(999.dp))
        ShimmerBox(width = 240.dp, height = 28.dp, base = base, shape = RoundedCornerShape(8.dp))
        ShimmerBox(width = 180.dp, height = 14.dp, base = base, shape = RoundedCornerShape(6.dp))
    }
}

@Composable
private fun ActionsRowPlaceholder(base: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(base)
        )
        ShimmerBox(width = 50.dp, height = 50.dp, base = base, shape = CircleShape)
        ShimmerBox(width = 44.dp, height = 44.dp, base = base, shape = CircleShape)
        ShimmerBox(width = 44.dp, height = 44.dp, base = base, shape = CircleShape)
    }
}

@Composable
private fun StatsPlaceholder(base: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(base)
    )
}

@Composable
private fun SongsListPlaceholder(base: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(base)
            )
        }
    }
}

@Composable
private fun ShimmerBox(
    width: Dp,
    height: Dp,
    base: Color,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(6.dp)
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(shape)
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
