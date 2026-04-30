package com.essence.essenceapp.feature.album.ui.components

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun AlbumDetailLoadingState(modifier: Modifier = Modifier) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val baseColor = rememberShimmerColor()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
            .padding(top = statusBarTopPadding + 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TopBarSkeleton(base = baseColor)

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(212.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(baseColor)
        )

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(width = 90.dp, height = 22.dp, base = baseColor, shape = 999.dp)
            ShimmerBox(width = 220.dp, height = 26.dp, base = baseColor)
            ShimmerBox(width = 140.dp, height = 14.dp, base = baseColor)
        }

        StatsSkeleton(base = baseColor)
        ActionsSkeleton(base = baseColor)
        TracksSkeleton(base = baseColor)
    }
}

@Composable
private fun TopBarSkeleton(base: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(50))
                .background(base)
        )
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(50))
                .background(base)
        )
    }
}

@Composable
private fun StatsSkeleton(base: Color) {
    GlassIsland(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = MutedTeal,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ShimmerBox(width = 18.dp, height = 18.dp, base = base, shape = 999.dp)
                    ShimmerBox(width = 36.dp, height = 14.dp, base = base)
                    ShimmerBox(width = 48.dp, height = 10.dp, base = base)
                }
            }
        }
    }
}

@Composable
private fun ActionsSkeleton(base: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(base)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(base)
        )
    }
}

@Composable
private fun TracksSkeleton(base: Color) {
    GlassIsland(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = SoftRose,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(5) { index ->
                TrackRowSkeleton(base = base)
                if (index < 4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(0.5.dp)
                            .background(PureWhite.copy(alpha = 0.04f))
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackRowSkeleton(base: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(width = 22.dp, height = 14.dp, base = base)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(base)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBox(width = 170.dp, height = 12.dp, base = base)
            ShimmerBox(width = 110.dp, height = 10.dp, base = base)
        }
        Spacer(modifier = Modifier.width(10.dp))
        ShimmerBox(width = 32.dp, height = 10.dp, base = base)
    }
}

@Composable
private fun rememberShimmerColor(): Color {
    val transition = rememberInfiniteTransition(label = "album_loading_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    return PureWhite.copy(alpha = alpha)
}

@Composable
private fun ShimmerBox(
    width: Dp,
    height: Dp,
    base: Color,
    shape: Dp = 6.dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(shape))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        base.copy(alpha = base.alpha * 0.6f),
                        base,
                        base.copy(alpha = base.alpha * 0.6f)
                    )
                )
            )
    )
}
