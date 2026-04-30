package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.playback.components.PlaybackManagerContent
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun LoadingState(
    modifier: Modifier = Modifier,
    onPlaybackAction: (PlaybackAction) -> Unit = {}
) {
    val brush = rememberShimmerBrush()
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val safeBottom = navBottom + 20.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = safeBottom),
            userScrollEnabled = false
        ) {
            item {
                val heroHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.60f)
                    .coerceAtMost(620.dp) + 20.dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heroHeight)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        SoftRose.copy(alpha = 0.28f),
                                        MutedTeal.copy(alpha = 0.18f),
                                        MidnightBlack.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(brush)
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MidnightBlack.copy(alpha = 0.05f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        MidnightBlack.copy(alpha = 0.24f),
                                        MidnightBlack.copy(alpha = 0.58f),
                                        MidnightBlack.copy(alpha = 0.90f),
                                        MidnightBlack
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
                                        SoftRose.copy(alpha = 0.08f),
                                        Color.Transparent
                                    ),
                                    radius = 1100f
                                )
                            )
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = 16.dp,
                                top = statusBarTopPadding + 12.dp
                            ),
                        shape = CircleShape,
                        color = GraphiteSurface.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
                    ) {
                        Box(modifier = Modifier.size(44.dp))
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(
                                end = 16.dp,
                                top = statusBarTopPadding + 12.dp
                            ),
                        shape = CircleShape,
                        color = GraphiteSurface.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
                    ) {
                        Box(modifier = Modifier.size(44.dp))
                    }

                    GlassIsland(
                        accent = SoftRose,
                        isPulsing = false,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = 32.dp, end = 32.dp, bottom = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ShimmerBox(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(24.dp),
                                brush = brush,
                                shape = RoundedCornerShape(8.dp)
                            )
                            ShimmerBox(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(14.dp),
                                brush = brush,
                                shape = RoundedCornerShape(6.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(52.dp)
                                        .height(20.dp),
                                    brush = brush,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .height(20.dp),
                                    brush = brush,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(20.dp),
                                    brush = brush,
                                    shape = RoundedCornerShape(999.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                PlaybackManagerContent(
                    state = PlaybackUiState(isBuffering = true),
                    onAction = onPlaybackAction,
                    showMetaHeader = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))

                GlassIsland(
                    accent = MutedTeal,
                    isPulsing = false,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        DetailsShimmerRow(
                            brush = brush,
                            iconAccent = LuxeGold,
                            labelWidth = 50.dp,
                            valueWidth = 160.dp
                        )
                        DetailsShimmerRow(
                            brush = brush,
                            iconAccent = MutedTeal,
                            labelWidth = 60.dp,
                            valueWidth = 120.dp
                        )
                        DetailsShimmerRow(
                            brush = brush,
                            iconAccent = SoftRose,
                            labelWidth = 70.dp,
                            valueWidth = 90.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsShimmerRow(
    brush: Brush,
    iconAccent: Color,
    labelWidth: Dp,
    valueWidth: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(12.dp),
            color = iconAccent.copy(alpha = 0.10f)
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(labelWidth)
                    .height(10.dp),
                brush = brush
            )
            ShimmerBox(
                modifier = Modifier
                    .width(valueWidth)
                    .height(13.dp),
                brush = brush
            )
        }
    }
}

@Composable
internal fun LoadingNextSongState(
    modifier: Modifier = Modifier,
    title: String,
    artistName: String,
    imageKey: String?,
    playback: PlaybackUiState,
    onPlaybackAction: (PlaybackAction) -> Unit
) {
    val imageUrl = resolveImageUrl(imageKey)
    val brush = rememberShimmerBrush()
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val safeBottom = navBottom + 20.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = safeBottom),
            userScrollEnabled = false
        ) {
            item {
                val heroHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.60f).coerceAtMost(620.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heroHeight)
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .size(Size.ORIGINAL)
                                .crossfade(true)
                                .build(),
                            contentDescription = title,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            SoftRose.copy(alpha = 0.28f),
                                            MutedTeal.copy(alpha = 0.18f),
                                            MidnightBlack.copy(alpha = 0.95f)
                                        )
                                    )
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MidnightBlack.copy(alpha = 0.05f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Transparent,
                                        MidnightBlack.copy(alpha = 0.01f),
                                        MidnightBlack.copy(alpha = 0.03f),
                                        MidnightBlack.copy(alpha = 0.06f),
                                        MidnightBlack.copy(alpha = 0.10f),
                                        MidnightBlack.copy(alpha = 0.16f),
                                        MidnightBlack.copy(alpha = 0.24f),
                                        MidnightBlack.copy(alpha = 0.34f),
                                        MidnightBlack.copy(alpha = 0.46f),
                                        MidnightBlack.copy(alpha = 0.58f),
                                        MidnightBlack.copy(alpha = 0.70f),
                                        MidnightBlack.copy(alpha = 0.82f),
                                        MidnightBlack.copy(alpha = 0.90f),
                                        MidnightBlack.copy(alpha = 0.96f),
                                        MidnightBlack
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
                                        SoftRose.copy(alpha = 0.08f),
                                        Color.Transparent
                                    ),
                                    radius = 1100f
                                )
                            )
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 16.dp, top = statusBarTopPadding + 12.dp),
                        shape = CircleShape,
                        color = GraphiteSurface.copy(alpha = 0.40f)
                    ) {
                        Box(modifier = Modifier.size(44.dp))
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, end = 20.dp, bottom = 48.dp)
                    ) {
                        GlassIsland(
                            accent = SoftRose,
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                if (artistName.isNotBlank()) {
                                    Text(
                                        text = artistName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = PureWhite.copy(alpha = 0.76f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                PlaybackManagerContent(
                    state = playback.copy(isBuffering = true),
                    onAction = onPlaybackAction,
                    showMetaHeader = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = GraphiteSurface.copy(alpha = 0.40f),
                    border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.07f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        repeat(3) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                ShimmerBox(
                                    modifier = Modifier.size(36.dp),
                                    brush = brush,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ShimmerBox(
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(10.dp),
                                        brush = brush
                                    )
                                    ShimmerBox(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(13.dp),
                                        brush = brush
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    return Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            PureWhite.copy(alpha = 0.06f),
            PureWhite.copy(alpha = 0.12f),
            PureWhite.copy(alpha = 0.06f),
            Color.Transparent
        ),
        startX = translateAnim - 220f,
        endX = translateAnim + 220f
    )
}

@Composable
internal fun ShimmerBox(
    modifier: Modifier = Modifier,
    brush: Brush,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(GraphiteSurface.copy(alpha = 0.38f))
            .background(brush)
    )
}
