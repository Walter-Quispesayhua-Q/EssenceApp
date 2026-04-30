package com.essence.essenceapp.feature.search.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun SearchResultsSkeleton(
    modifier: Modifier = Modifier,
    showSongs: Boolean = true,
    showAlbums: Boolean = true,
    showArtists: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(top = 16.dp, bottom = 24.dp)
) {
    val brush = rememberSearchShimmerBrush()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        if (showSongs) {
            SkeletonSectionHeader(brush = brush, accent = SoftRose)
            SongsIslandSkeleton(brush = brush, rowCount = 4)
        }

        if (showAlbums) {
            SkeletonSectionHeader(brush = brush, accent = SoftRose)
            AlbumsRowSkeleton(brush = brush, count = 5)
        }

        if (showArtists) {
            SkeletonSectionHeader(brush = brush, accent = SoftRose)
            ArtistsRowSkeleton(brush = brush, count = 5)
        }
    }
}

@Composable
fun SongRowsSkeleton(
    modifier: Modifier = Modifier,
    rowCount: Int = 3
) {
    val brush = rememberSearchShimmerBrush()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SongsIslandSkeleton(brush = brush, rowCount = rowCount)
    }
}

@Composable
private fun SkeletonSectionHeader(brush: Brush, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(accent, accent.copy(alpha = 0.3f))
                    )
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        ShimmerBlock(width = 110.dp, height = 16.dp, brush = brush)
        Spacer(modifier = Modifier.weight(1f))
        ShimmerBlock(width = 28.dp, height = 12.dp, brush = brush)
    }
}

@Composable
private fun SongsIslandSkeleton(brush: Brush, rowCount: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            repeat(rowCount) { index ->
                SongRowSkeleton(brush = brush)
                if (index < rowCount - 1) {
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
private fun SongRowSkeleton(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBlock(
            width = 52.dp,
            height = 52.dp,
            brush = brush,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            ShimmerBlock(width = 180.dp, height = 13.dp, brush = brush)
            ShimmerBlock(width = 110.dp, height = 11.dp, brush = brush)
        }
        Spacer(modifier = Modifier.width(12.dp))
        ShimmerBlock(
            width = 28.dp,
            height = 28.dp,
            brush = brush,
            shape = CircleShape
        )
    }
}

@Composable
private fun AlbumsRowSkeleton(brush: Brush, count: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(count) { _ ->
                    AlbumCardSkeleton(brush = brush)
                }
            }
        }
    }
}

@Composable
private fun AlbumCardSkeleton(brush: Brush) {
    Column(
        modifier = Modifier.width(132.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBlock(
            width = 132.dp,
            height = 132.dp,
            brush = brush,
            shape = RoundedCornerShape(14.dp)
        )
        ShimmerBlock(width = 110.dp, height = 12.dp, brush = brush)
        ShimmerBlock(width = 70.dp, height = 10.dp, brush = brush)
    }
}

@Composable
private fun ArtistsRowSkeleton(brush: Brush, count: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(count) { _ ->
                    ArtistCircleSkeleton(brush = brush)
                }
            }
        }
    }
}

@Composable
private fun ArtistCircleSkeleton(brush: Brush) {
    Column(
        modifier = Modifier.width(108.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ShimmerBlock(
            width = 108.dp,
            height = 108.dp,
            brush = brush,
            shape = CircleShape
        )
        ShimmerBlock(width = 84.dp, height = 12.dp, brush = brush)
    }
}

@Composable
private fun ShimmerBlock(
    width: Dp,
    height: Dp,
    brush: Brush,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(GraphiteSurface.copy(alpha = 0.42f))
            .background(brush)
    )
}

@Composable
private fun rememberSearchShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "search_shimmer")
    val translate by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "search_shimmer_translate"
    )
    return Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            PureWhite.copy(alpha = 0.06f),
            PureWhite.copy(alpha = 0.12f),
            PureWhite.copy(alpha = 0.06f),
            Color.Transparent
        ),
        startX = translate - 220f,
        endX = translate + 220f
    )
}

@Preview(name = "Search - Results Skeleton", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SearchResultsSkeletonPreview() {
    EssenceAppTheme {
        SearchResultsSkeleton()
    }
}

@Preview(name = "Search - Songs Skeleton", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SongRowsSkeletonPreview() {
    EssenceAppTheme {
        SongRowsSkeleton(rowCount = 3)
    }
}
