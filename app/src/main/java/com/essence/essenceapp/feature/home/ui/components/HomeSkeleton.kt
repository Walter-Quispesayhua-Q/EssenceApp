package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface

@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        ShimmerBox(
            modifier = Modifier
                .width(180.dp)
                .height(18.dp),
            brush = brush
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(4) { SkeletonSongRow(brush = brush) }
            }
        }

        ShimmerBox(
            modifier = Modifier
                .width(200.dp)
                .height(18.dp),
            brush = brush
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { SkeletonAlbumCard(brush = brush) }
        }

        ShimmerBox(
            modifier = Modifier
                .width(190.dp)
                .height(18.dp),
            brush = brush
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { SkeletonArtistCircle(brush = brush) }
        }
    }
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        GraphiteSurface.copy(alpha = 0.6f),
        GraphiteSurface.copy(alpha = 0.2f),
        GraphiteSurface.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )
}

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier,
    brush: Brush,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
private fun SkeletonSongRow(brush: Brush) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(
            modifier = Modifier.size(45.dp),
            brush = brush,
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .width(160.dp)
                    .height(14.dp),
                brush = brush
            )
            ShimmerBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(11.dp),
                brush = brush
            )
        }
    }
}

@Composable
private fun SkeletonAlbumCard(brush: Brush) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBox(
            modifier = Modifier.size(100.dp),
            brush = brush,
            shape = RoundedCornerShape(12.dp)
        )
        ShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(12.dp),
            brush = brush
        )
    }
}

@Composable
private fun SkeletonArtistCircle(brush: Brush) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBox(
            modifier = Modifier.size(80.dp),
            brush = brush,
            shape = CircleShape
        )
        ShimmerBox(
            modifier = Modifier
                .width(60.dp)
                .height(12.dp),
            brush = brush
        )
    }
}

@Preview(name = "HomeSkeleton", showBackground = true)
@Composable
private fun HomeSkeletonPreview() {
    EssenceAppTheme {
        HomeSkeleton()
    }
}
