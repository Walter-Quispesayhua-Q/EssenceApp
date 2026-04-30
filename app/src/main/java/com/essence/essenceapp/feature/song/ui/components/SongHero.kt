package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioOutputType
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.util.Locale
import kotlin.math.pow

private const val ARTWORK_MAX_DP = 520
private const val PULSE_CYCLE_MS = 1800
private const val PULSE_TARGET_SCALE = 1.005f
private val ISLAND_OVERLAP = 48.dp
private val ISLAND_HORIZONTAL = 16.dp
private const val TAG_ROTATION_DEG = 30f

@Composable
internal fun SongHero(
    song: Song,
    audioOutput: AudioOutputType,
    isPulsing: Boolean,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val artworkHeight = screenWidthDp.coerceAtMost(ARTWORK_MAX_DP.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        HeroArtwork(
            imageKey = song.imageKey,
            contentDescription = song.title,
            isPulsing = isPulsing,
            modifier = Modifier
                .fillMaxWidth()
                .height(artworkHeight)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = artworkHeight - ISLAND_OVERLAP,
                    start = ISLAND_HORIZONTAL,
                    end = ISLAND_HORIZONTAL
                )
        ) {
            TitleIsland(
                song = song,
                onArtistClick = onArtistClick,
                modifier = Modifier.fillMaxWidth()
            )

            AudioOutputTag(
                audioOutput = audioOutput,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 4.dp, y = (-14).dp)
                    .rotate(-TAG_ROTATION_DEG)
            )

            AnimatedVisibility(
                visible = song.isLiked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = (-14).dp)
                    .rotate(TAG_ROTATION_DEG),
                enter = scaleIn(
                    initialScale = 0.5f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(durationMillis = 380)),
                exit = scaleOut(
                    targetScale = 0.6f,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(durationMillis = 220))
            ) {
                LikeTag()
            }
        }
    }
}

@Composable
private fun TitleIsland(
    song: Song,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassIsland(
        modifier = modifier.fillMaxWidth(),
        accent = LuxeGold,
        isPulsing = false,
        accentAlpha = 0.05f,
        materialAlpha = 0.55f,
        contentPadding = PaddingValues(
            top = 22.dp,
            bottom = 18.dp,
            start = 20.dp,
            end = 20.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 340.dp)
            )

            if (song.artists.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                ArtistsLine(
                    artists = song.artists.map { it.nameArtist to it.detailLookup },
                    onArtistClick = onArtistClick
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            MetaPillsRow(song = song)
        }
    }
}

@Composable
private fun HeroArtwork(
    imageKey: String?,
    contentDescription: String,
    isPulsing: Boolean,
    modifier: Modifier = Modifier
) {
    val imageUrl = resolveImageUrl(imageKey)

    val pulseAnimatable = remember { Animatable(1f) }
    LaunchedEffect(isPulsing) {
        if (isPulsing) {
            while (true) {
                pulseAnimatable.animateTo(
                    targetValue = PULSE_TARGET_SCALE,
                    animationSpec = tween(PULSE_CYCLE_MS, easing = FastOutSlowInEasing)
                )
                pulseAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(PULSE_CYCLE_MS, easing = FastOutSlowInEasing)
                )
            }
        }
    }
    val pulseScale = pulseAnimatable.value

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
            .heroBottomFade()
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.32f),
                                MutedTeal.copy(alpha = 0.22f),
                                GraphiteSurface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = PureWhite.copy(alpha = 0.42f),
                    modifier = Modifier.size(96.dp)
                )
            }
        }
    }
}

private fun Modifier.heroBottomFade(): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithCache {
        val steps = 20
        val maskStops = Array(steps + 1) { i ->
            val t = i.toFloat() / steps
            val opacity = (1.0 - t.toDouble().pow(7.0)).toFloat().coerceIn(0f, 1f)
            t to Color.White.copy(alpha = opacity)
        }
        val maskBrush = Brush.verticalGradient(colorStops = maskStops)
        onDrawWithContent {
            drawContent()
            drawRect(brush = maskBrush, blendMode = BlendMode.DstIn)
        }
    }

@Composable
private fun AudioOutputTag(
    audioOutput: AudioOutputType,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MidnightBlack.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.70f))
    ) {
        Icon(
            imageVector = audioOutput.icon,
            contentDescription = audioOutput.label,
            tint = MutedTeal,
            modifier = Modifier
                .padding(8.dp)
                .size(16.dp)
        )
    }
}

@Composable
private fun LikeTag(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = SoftRose.copy(alpha = 0.22f),
        border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.80f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorita",
                tint = SoftRose,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Favorita",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = SoftRose.copy(alpha = 0.95f)
            )
        }
    }
}

@Composable
private fun ArtistsLine(
    artists: List<Pair<String, String>>,
    onArtistClick: (String) -> Unit
) {
    val text = artists.joinToString(", ") { it.first }
    val isSingle = artists.size == 1

    if (isSingle) {
        val (name, lookup) = artists.first()
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.82f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onArtistClick(lookup) }
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.82f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 340.dp)
        )
    }
}

@Composable
private fun MetaPillsRow(song: Song) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        song.songType?.takeIf { it.isNotBlank() }?.let {
            MetaPill(text = it.replaceFirstChar { c -> c.uppercase() }, accent = MutedTeal)
        }
        song.releaseDate?.let {
            MetaPill(text = it.year.toString(), accent = SoftRose)
        }
        song.totalPlays?.let {
            MetaPill(
                text = formatPlays(it),
                accent = LuxeGold,
                icon = Icons.Default.PlayArrow
            )
        }
    }
}

@Composable
private fun MetaPill(
    text: String,
    accent: Color,
    icon: ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent.copy(alpha = 0.90f),
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = accent.copy(alpha = 0.90f)
            )
        }
    }
}

private fun formatPlays(value: Int): String {
    return when {
        value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}
