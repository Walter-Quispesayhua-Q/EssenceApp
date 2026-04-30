package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import kotlinx.coroutines.delay

private enum class QueueStateKind { Empty, List }

@Composable
internal fun SongQueueIsland(
    items: List<PlaybackQueueItem>,
    currentIndex: Int,
    isPulsing: Boolean,
    isBuffering: Boolean,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassIsland(
        accent = MutedTeal,
        isPulsing = isPulsing,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
        modifier = modifier
    ) {
        QueueContainer(
            items = items,
            currentIndex = currentIndex,
            isBuffering = isBuffering,
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun QueueContainer(
    items: List<PlaybackQueueItem>,
    currentIndex: Int,
    isBuffering: Boolean,
    onItemClick: (Int) -> Unit
) {
    var pendingIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val isSwitching = isBuffering || pendingIndex != null

    LaunchedEffect(currentIndex) {
        if (pendingIndex == currentIndex) pendingIndex = null
    }

    LaunchedEffect(isBuffering, pendingIndex) {
        if (pendingIndex != null && !isBuffering) {
            delay(900L)
            if (pendingIndex != null) pendingIndex = null
        }
    }

    val kind = if (items.isEmpty()) QueueStateKind.Empty else QueueStateKind.List

    Column(modifier = Modifier.fillMaxWidth()) {
        QueueHeader(
            totalCount = items.size,
            isSwitching = isSwitching
        )

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedContent(
            targetState = kind,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith
                        fadeOut(animationSpec = tween(220))
            },
            label = "queue_kind"
        ) { state ->
            when (state) {
                QueueStateKind.Empty -> QueueEmpty()
                QueueStateKind.List -> QueueList(
                    items = items,
                    currentIndex = currentIndex,
                    pendingIndex = pendingIndex,
                    isSwitching = isSwitching,
                    onItemClick = { index ->
                        if (isSwitching || index == currentIndex) return@QueueList
                        pendingIndex = index
                        onItemClick(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun QueueHeader(
    totalCount: Int,
    isSwitching: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MutedTeal.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.22f))
        ) {
            Box(
                modifier = Modifier.padding(7.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = null,
                    tint = MutedTeal,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "Cola de reproduccion",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite.copy(alpha = 0.88f)
            )
            Text(
                text = when {
                    isSwitching -> "Sincronizando"
                    totalCount == 0 -> "Sin canciones"
                    totalCount == 1 -> "1 cancion"
                    else -> "$totalCount canciones"
                },
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.44f)
            )
        }

        if (isSwitching) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 1.6.dp,
                color = MutedTeal
            )
        }
    }
}

@Composable
private fun QueueEmpty() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = PureWhite.copy(alpha = 0.20f),
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Sin cola de reproduccion",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.32f)
            )
        }
    }
}

@Composable
private fun QueueList(
    items: List<PlaybackQueueItem>,
    currentIndex: Int,
    pendingIndex: Int?,
    isSwitching: Boolean,
    onItemClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0 && items.isNotEmpty()) {
            val target = (currentIndex - 1).coerceAtLeast(0)
            listState.animateScrollToItem(
                index = target.coerceAtMost(items.lastIndex),
                scrollOffset = 0
            )
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(items) { index, item ->
            val isCurrent = index == currentIndex
            val isPast = index < currentIndex
            val isPending = pendingIndex == index

            QueueRow(
                item = item,
                isCurrent = isCurrent,
                isPast = isPast,
                isPending = isPending,
                isInteractionLocked = isSwitching,
                onClick = { onItemClick(index) }
            )
        }
    }
}

@Composable
private fun QueueRow(
    item: PlaybackQueueItem,
    isCurrent: Boolean,
    isPast: Boolean,
    isPending: Boolean,
    isInteractionLocked: Boolean,
    onClick: () -> Unit
) {
    val contentAlpha = when {
        isPending -> 0.96f
        isCurrent -> 1f
        isPast -> 0.38f
        else -> 0.78f
    }

    val bgBrush = when {
        isPending -> Brush.horizontalGradient(
            colors = listOf(
                MutedTeal.copy(alpha = 0.20f),
                MutedTeal.copy(alpha = 0.06f)
            )
        )
        isCurrent -> Brush.horizontalGradient(
            colors = listOf(
                MutedTeal.copy(alpha = 0.14f),
                MutedTeal.copy(alpha = 0.02f)
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    val borderColor = when {
        isPending -> MutedTeal.copy(alpha = 0.32f)
        isCurrent -> MutedTeal.copy(alpha = 0.18f)
        else -> Color.Transparent
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                enabled = !isInteractionLocked && !isCurrent,
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = if (isPending || isCurrent) BorderStroke(0.5.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgBrush)
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActiveBar(isVisible = isCurrent || isPending)

            CoverWithEqualizer(
                item = item,
                isCurrent = isCurrent,
                isPending = isPending,
                contentAlpha = contentAlpha
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent || isPending) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent || isPending) MutedTeal else PureWhite.copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.artistName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent || isPending) MutedTeal.copy(alpha = 0.68f)
                    else PureWhite.copy(alpha = 0.40f * contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TrailingStatus(
                isPending = isPending,
                durationMs = item.durationMs,
                contentAlpha = contentAlpha
            )
        }
    }
}

@Composable
private fun ActiveBar(isVisible: Boolean) {
    if (!isVisible) {
        Spacer(modifier = Modifier.width(3.dp))
        return
    }
    Box(
        modifier = Modifier
            .size(width = 3.dp, height = 34.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.90f),
                        MutedTeal.copy(alpha = 0.30f)
                    )
                )
            )
    )
}

@Composable
private fun CoverWithEqualizer(
    item: PlaybackQueueItem,
    isCurrent: Boolean,
    isPending: Boolean,
    contentAlpha: Float
) {
    Box(
        modifier = Modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        val imageUrl = resolveImageUrl(item.imageKey)
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop,
                alpha = contentAlpha
            )
        } else {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = GraphiteSurface.copy(alpha = 0.60f),
                border = BorderStroke(0.5.dp, MutedTeal.copy(alpha = 0.10f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = PureWhite.copy(alpha = 0.30f * contentAlpha),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        if (isCurrent && !isPending) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MidnightBlack.copy(alpha = 0.60f)),
                contentAlignment = Alignment.Center
            ) {
                EqualizerIcon()
            }
        }
    }
}

@Composable
private fun TrailingStatus(
    isPending: Boolean,
    durationMs: Long,
    contentAlpha: Float
) {
    if (isPending) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(10.dp),
                strokeWidth = 1.6.dp,
                color = MutedTeal
            )
            Text(
                text = "Cargando",
                style = MaterialTheme.typography.labelSmall,
                color = MutedTeal.copy(alpha = 0.82f)
            )
        }
    } else {
        Text(
            text = formatQueueDuration(durationMs),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.32f * contentAlpha)
        )
    }
}

@Composable
private fun EqualizerIcon() {
    val transition = rememberInfiniteTransition(label = "equalizer")

    val bar1 = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2 = transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3 = transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(16.dp)
    ) {
        listOf(bar1, bar2, bar3).forEach { anim ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp * anim.value)
                    .background(MutedTeal, RoundedCornerShape(1.dp))
            )
        }
    }
}

private fun formatQueueDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
