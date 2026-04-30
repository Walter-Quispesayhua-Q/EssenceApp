package com.essence.essenceapp.feature.profile.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

private val ScreenHorizontalPadding = 20.dp
private val IslandRadius = 28.dp
private const val ShimmerWidth = 600f
private const val ShimmerTravel = 1400f
private const val ShimmerDurationMillis = 1500

@Composable
fun ProfileLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    val bottomClearance = LocalBottomBarClearance.current
    val shimmerBrush = rememberShimmerBrush()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomClearance + 24.dp)
        ) {
            item { HeroSectionSkeleton(shimmerBrush = shimmerBrush) }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                SummaryIslandSkeleton(
                    shimmerBrush = shimmerBrush,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))
                SectionHeaderSkeleton(
                    shimmerBrush = shimmerBrush,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                StatsMatrixSkeleton(
                    shimmerBrush = shimmerBrush,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))
                AccountDetailsSkeleton(
                    shimmerBrush = shimmerBrush,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }
        }
    }
}

@Composable
private fun HeroSectionSkeleton(shimmerBrush: Brush) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidnightBlack)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.18f),
                            SoftRose.copy(alpha = 0.12f),
                            MidnightBlack.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = statusBarTopPadding + 12.dp,
                    bottom = 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ShimmerBlock(
                brush = shimmerBrush,
                width = 130.dp,
                height = 28.dp,
                shape = RoundedCornerShape(999.dp)
            )

            AvatarOrbSkeleton(shimmerBrush = shimmerBrush)

            HeroInfoIslandSkeleton(shimmerBrush = shimmerBrush)
        }
    }
}

@Composable
private fun AvatarOrbSkeleton(shimmerBrush: Brush) {
    Box(
        modifier = Modifier.size(154.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.16f),
                            MutedTeal.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier.size(132.dp),
            shape = CircleShape,
            color = GraphiteSurface.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f)),
            shadowElevation = 16.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(shimmerBrush)
                )
            }
        }
    }
}

@Composable
private fun HeroInfoIslandSkeleton(shimmerBrush: Brush) {
    GlassIslandSkeleton(
        modifier = Modifier.fillMaxWidth(),
        accent = SoftRose,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShimmerBlock(
                brush = shimmerBrush,
                width = 200.dp,
                height = 28.dp
            )

            ShimmerBlock(
                brush = shimmerBrush,
                width = 180.dp,
                height = 16.dp
            )

            ShimmerBlock(
                brush = shimmerBrush,
                width = 140.dp,
                height = 12.dp
            )

            Spacer(modifier = Modifier.height(4.dp))

            HeroMetricsGridSkeleton(shimmerBrush = shimmerBrush)
        }
    }
}

@Composable
private fun HeroMetricsGridSkeleton(shimmerBrush: Brush) {
    val accents = listOf(
        MutedTeal,
        LuxeGold,
        SoftRose,
        MutedTeal
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        accents.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { accent ->
                    HeroMetricCardSkeleton(
                        modifier = Modifier.weight(1f),
                        accent = accent,
                        shimmerBrush = shimmerBrush
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroMetricCardSkeleton(
    modifier: Modifier = Modifier,
    accent: Color,
    shimmerBrush: Brush
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBlock(
                brush = shimmerBrush,
                width = 60.dp,
                height = 12.dp
            )

            ShimmerBlock(
                brush = shimmerBrush,
                width = 84.dp,
                height = 18.dp
            )
        }
    }
}

@Composable
private fun SummaryIslandSkeleton(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    GlassIslandSkeleton(
        modifier = modifier,
        accent = MutedTeal,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShimmerBlock(
                brush = shimmerBrush,
                width = 130.dp,
                height = 24.dp,
                shape = RoundedCornerShape(999.dp)
            )

            ShimmerBlock(
                brush = shimmerBrush,
                width = 240.dp,
                height = 20.dp
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerBlock(
                    brush = shimmerBrush,
                    fillWidth = true,
                    height = 14.dp
                )
                ShimmerBlock(
                    brush = shimmerBrush,
                    fillWidth = true,
                    height = 14.dp
                )
                ShimmerBlock(
                    brush = shimmerBrush,
                    width = 200.dp,
                    height = 14.dp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryHighlightPillSkeleton(
                        modifier = Modifier.weight(1f),
                        accent = SoftRose,
                        shimmerBrush = shimmerBrush
                    )
                    SummaryHighlightPillSkeleton(
                        modifier = Modifier.weight(1f),
                        accent = MutedTeal,
                        shimmerBrush = shimmerBrush
                    )
                }
                SummaryHighlightPillSkeleton(
                    modifier = Modifier.fillMaxWidth(),
                    accent = LuxeGold,
                    shimmerBrush = shimmerBrush
                )
            }
        }
    }
}

@Composable
private fun SummaryHighlightPillSkeleton(
    modifier: Modifier = Modifier,
    accent: Color,
    shimmerBrush: Brush
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBlock(
                brush = shimmerBrush,
                width = 70.dp,
                height = 12.dp
            )
            ShimmerBlock(
                brush = shimmerBrush,
                width = 110.dp,
                height = 16.dp
            )
        }
    }
}

@Composable
private fun SectionHeaderSkeleton(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(LuxeGold.copy(alpha = 0.6f))
            )
            Spacer(modifier = Modifier.width(10.dp))
            ShimmerBlock(
                brush = shimmerBrush,
                width = 200.dp,
                height = 22.dp
            )
        }
        ShimmerBlock(
            brush = shimmerBrush,
            width = 280.dp,
            height = 14.dp
        )
    }
}

@Composable
private fun StatsMatrixSkeleton(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    val accents = listOf(SoftRose, LuxeGold, MutedTeal, SoftRose, LuxeGold)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        accents.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { accent ->
                    StatMetricCardSkeleton(
                        modifier = Modifier.weight(1f),
                        accent = accent,
                        shimmerBrush = shimmerBrush
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatMetricCardSkeleton(
    modifier: Modifier = Modifier,
    accent: Color,
    shimmerBrush: Brush
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        color = GraphiteSurface.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.04f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerBlock(
                        brush = shimmerBrush,
                        width = 64.dp,
                        height = 18.dp,
                        shape = RoundedCornerShape(999.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.8f))
                    )
                }

                ShimmerBlock(
                    brush = shimmerBrush,
                    width = 90.dp,
                    height = 32.dp
                )

                ShimmerBlock(
                    brush = shimmerBrush,
                    width = 130.dp,
                    height = 14.dp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.65f),
                                    accent.copy(alpha = 0.20f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun AccountDetailsSkeleton(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    GlassIslandSkeleton(
        modifier = modifier,
        accent = LuxeGold,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShimmerBlock(
                brush = shimmerBrush,
                width = 170.dp,
                height = 22.dp
            )

            DetailRowSkeleton(shimmerBrush = shimmerBrush, accent = MutedTeal)
            DetailRowSkeleton(shimmerBrush = shimmerBrush, accent = SoftRose)
            DetailRowSkeleton(shimmerBrush = shimmerBrush, accent = LuxeGold)
        }
    }
}

@Composable
private fun DetailRowSkeleton(
    shimmerBrush: Brush,
    accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShimmerBlock(
                    brush = shimmerBrush,
                    width = 70.dp,
                    height = 12.dp
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent.copy(alpha = 0.65f))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            ShimmerBlock(
                modifier = Modifier.weight(1f),
                brush = shimmerBrush,
                fillWidth = true,
                height = 16.dp
            )
        }
    }
}

@Composable
private fun GlassIslandSkeleton(
    modifier: Modifier = Modifier,
    accent: Color,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(IslandRadius),
        color = GraphiteSurface.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.04f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}

@Composable
private fun ShimmerBlock(
    brush: Brush,
    modifier: Modifier = Modifier,
    width: Dp = 0.dp,
    height: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    fillWidth: Boolean = false
) {
    val sized = when {
        fillWidth -> modifier.fillMaxWidth().height(height)
        width > 0.dp -> modifier.size(width = width, height = height)
        else -> modifier.height(height)
    }

    Box(
        modifier = sized
            .clip(shape)
            .background(brush)
    )
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "profile_shimmer")
    val translate by transition.animateFloat(
        initialValue = -ShimmerWidth,
        targetValue = ShimmerTravel,
        animationSpec = infiniteRepeatable(
            animation = tween(ShimmerDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            PureWhite.copy(alpha = 0.05f),
            PureWhite.copy(alpha = 0.16f),
            PureWhite.copy(alpha = 0.05f)
        ),
        start = Offset(translate, 0f),
        end = Offset(translate + ShimmerWidth, 0f)
    )
}

@Preview(name = "Profile Skeleton", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProfileSkeletonPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileLoadingSkeleton()
        }
    }
}
