package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.util.lerp
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.PureWhite
import kotlin.math.absoluteValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.SongDetailAction
import com.essence.essenceapp.feature.song.ui.SongDetailUiState
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.PlaybackRepeatMode
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.playback.components.PlaybackManagerContent
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate

@Composable
fun SongDetailContent(
    modifier: Modifier = Modifier,
    state: SongDetailUiState,
    onAction: (SongDetailAction) -> Unit,
    onPlaybackAction: (PlaybackAction) -> Unit
) {
    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density = currentDensity.density, fontScale = 1f)
    ) {
        when (state) {
            SongDetailUiState.Loading -> LoadingState(
                modifier = modifier,
                onPlaybackAction = onPlaybackAction
            )

            is SongDetailUiState.LoadingNextSong -> LoadingNextSongState(
                modifier = modifier,
                title = state.title,
                artistName = state.artistName,
                imageKey = state.imageKey,
                playback = state.playback,
                onPlaybackAction = onPlaybackAction
            )

            is SongDetailUiState.Error -> AppErrorState(
                modifier = modifier,
                message = state.message,
                title = "No se pudo cargar la cancion",
                onRetry = { onAction(SongDetailAction.Refresh) }
            )

            is SongDetailUiState.Success -> SuccessState(
                modifier = modifier,
                song = state.song,
                playback = state.playback,
                isLikeSubmitting = state.isLikeSubmitting,
                queueItems = state.queueItems,
                queueCurrentIndex = state.queueCurrentIndex,
                onAction = onAction,
                onPlaybackAction = onPlaybackAction
            )

            is SongDetailUiState.Unavailable -> UnavailableState(
                modifier = modifier,
                songTitle = state.songTitle
            )
        }
    }
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    song: Song,
    playback: PlaybackUiState,
    isLikeSubmitting: Boolean,
    queueItems: List<PlaybackQueueItem>,
    queueCurrentIndex: Int,
    onAction: (SongDetailAction) -> Unit,
    onPlaybackAction: (PlaybackAction) -> Unit
) {
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val safeBottom = navBottom + 20.dp
    val hasInfo = song.album != null || song.artists.isNotEmpty()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        SongAmbientBackground(imageKey = song.imageKey)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = safeBottom)
        ) {
            item {
                SongHero(
                    song = song,
                    audioOutput = playback.audioOutput,
                    isPulsing = playback.isPlaying && !playback.isBuffering,
                    onArtistClick = { lookup ->
                        onAction(SongDetailAction.OpenArtist(lookup))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                PlaybackManagerContent(
                    state = playback,
                    onAction = onPlaybackAction,
                    isLiked = song.isLiked,
                    isLikeSubmitting = isLikeSubmitting,
                    onToggleLike = { onAction(SongDetailAction.ToggleLike) },
                    showMetaHeader = false
                )
            }

            if (queueItems.isNotEmpty() || hasInfo) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    DetailSwipePager(
                        song = song,
                        queueItems = queueItems,
                        queueCurrentIndex = queueCurrentIndex,
                        isPulsing = playback.isPlaying && !playback.isBuffering,
                        isBuffering = playback.isBuffering,
                        onAction = onAction,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        SongTopBar(
            onBack = { onAction(SongDetailAction.Back) },
            onAddToPlaylist = { onAction(SongDetailAction.AddToPlaylist) },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun DetailSwipePager(
    song: Song,
    queueItems: List<com.essence.essenceapp.shared.playback.model.PlaybackQueueItem>,
    queueCurrentIndex: Int,
    isPulsing: Boolean,
    isBuffering: Boolean,
    onAction: (SongDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasQueue = queueItems.isNotEmpty()
    val hasInfo = song.album != null || song.artists.isNotEmpty()

    val pageKinds = remember(hasQueue, hasInfo) {
        buildList {
            if (hasQueue) add(SwipePage.Queue)
            if (hasInfo) add(SwipePage.Info)
        }
    }

    if (pageKinds.isEmpty()) return

    if (pageKinds.size == 1) {
        Box(modifier = modifier.fillMaxWidth()) {
            RenderSwipePage(
                kind = pageKinds.first(),
                song = song,
                queueItems = queueItems,
                queueCurrentIndex = queueCurrentIndex,
                isPulsing = isPulsing,
                isBuffering = isBuffering,
                onAction = onAction
            )
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = 0) { pageKinds.size }

    Column(modifier = modifier.fillMaxWidth()) {
        SubcomposeLayout(modifier = Modifier.fillMaxWidth()) { constraints ->
            val measuredHeights = pageKinds.map { kind ->
                subcompose(MeasureSlot(kind)) {
                    RenderSwipePage(
                        kind = kind,
                        song = song,
                        queueItems = queueItems,
                        queueCurrentIndex = queueCurrentIndex,
                        isPulsing = isPulsing,
                        isBuffering = isBuffering,
                        onAction = onAction
                    )
                }.firstOrNull()?.measure(constraints)?.height ?: 0
            }

            val currentPage = pagerState.currentPage.coerceIn(0, measuredHeights.lastIndex)
            val pageOffset = pagerState.currentPageOffsetFraction
            val neighborIndex = if (pageOffset >= 0f) {
                (currentPage + 1).coerceAtMost(measuredHeights.lastIndex)
            } else {
                (currentPage - 1).coerceAtLeast(0)
            }
            val fromHeight = measuredHeights[currentPage]
            val toHeight = measuredHeights[neighborIndex]
            val targetHeight = lerp(
                fromHeight.toFloat(),
                toHeight.toFloat(),
                pageOffset.absoluteValue.coerceIn(0f, 1f)
            ).toInt()

            val pagerPlaceable = subcompose(PagerSlot) {
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 16.dp,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIndex ->
                    val rawOffset = (
                        (pagerState.currentPage - pageIndex).toFloat() +
                            pagerState.currentPageOffsetFraction
                    )
                    val absOffset = rawOffset.absoluteValue.coerceIn(0f, 1f)
                    val easedOffset = FastOutSlowInEasing.transform(absOffset)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                val scale = lerp(0.90f, 1f, 1f - easedOffset)
                                scaleX = scale
                                scaleY = scale
                                alpha = lerp(0.45f, 1f, 1f - easedOffset)
                            }
                    ) {
                        RenderSwipePage(
                            kind = pageKinds[pageIndex],
                            song = song,
                            queueItems = queueItems,
                            queueCurrentIndex = queueCurrentIndex,
                            isPulsing = isPulsing,
                            isBuffering = isBuffering,
                            onAction = onAction
                        )
                    }
                }
            }.first().measure(
                constraints.copy(
                    minHeight = targetHeight,
                    maxHeight = targetHeight
                )
            )

            layout(pagerPlaceable.width, pagerPlaceable.height) {
                pagerPlaceable.placeRelative(0, 0)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        PagerDots(
            count = pageKinds.size,
            currentIndex = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private object PagerSlot
private data class MeasureSlot(val kind: SwipePage)

private enum class SwipePage { Queue, Info }

@Composable
private fun RenderSwipePage(
    kind: SwipePage,
    song: Song,
    queueItems: List<com.essence.essenceapp.shared.playback.model.PlaybackQueueItem>,
    queueCurrentIndex: Int,
    isPulsing: Boolean,
    isBuffering: Boolean,
    onAction: (SongDetailAction) -> Unit
) {
    when (kind) {
        SwipePage.Queue -> SongQueueIsland(
            items = queueItems,
            currentIndex = queueCurrentIndex,
            isPulsing = isPulsing,
            isBuffering = isBuffering,
            onItemClick = { index -> onAction(SongDetailAction.PlayQueueItem(index)) }
        )
        SwipePage.Info -> CombinedInfoIsland(
            album = song.album,
            artists = song.artists,
            onAlbumClick = { lookup -> onAction(SongDetailAction.OpenAlbum(lookup)) },
            onArtistClick = { lookup -> onAction(SongDetailAction.OpenArtist(lookup)) }
        )
    }
}

@Composable
private fun PagerDots(
    count: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(count) { index ->
            val selected = index == currentIndex
            val dotSize by animateDpAsState(
                targetValue = if (selected) 8.dp else 6.dp,
                label = "dot_size"
            )
            val color = if (selected) LuxeGold else PureWhite.copy(alpha = 0.22f)
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// PREVIEWS

private val previewSong = Song(
    id = 1L,
    title = "Titi Me Pregunto",
    durationMs = 210_000,
    hlsMasterKey = "songs/titi/master.m3u8",
    imageKey = null,
    songType = "single",
    totalPlays = 1_200_000,
    isLiked = true,
    artists = listOf(
        ArtistSimple(10L, "Bad Bunny", null, "artists/bad-bunny"),
        ArtistSimple(11L, "Chencho Corleone", null, "artists/chencho")
    ),
    album = AlbumSimple(
        20L,
        "Un Verano Sin Ti",
        null,
        "albums/uvst",
        listOf("Bad Bunny"),
        LocalDate.of(2022, 5, 6)
    ),
    releaseDate = LocalDate.of(2022, 5, 6),
    streamingUrl = null,
    streamingUrlExpiresAt = null
)

@Preview(name = "Song - Playing", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlayingPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Success(
                song = previewSong,
                playback = PlaybackUiState(
                    isPlaying = true,
                    positionMs = 72_000L,
                    durationMs = 210_000L,
                    repeatMode = PlaybackRepeatMode.One
                ),
                isLikeSubmitting = false
            ),
            onAction = {},
            onPlaybackAction = {}
        )
    }
}

@Preview(name = "Song - Paused", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PausedPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Success(
                song = previewSong,
                playback = PlaybackUiState(
                    isPlaying = false,
                    positionMs = 45_000L,
                    durationMs = 210_000L,
                    repeatMode = PlaybackRepeatMode.Off
                ),
                isLikeSubmitting = false
            ),
            onAction = {},
            onPlaybackAction = {}
        )
    }
}