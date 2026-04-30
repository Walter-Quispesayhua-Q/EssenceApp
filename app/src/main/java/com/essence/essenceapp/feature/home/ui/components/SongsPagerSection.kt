package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.playback.mapper.toQueueItems
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import com.essence.essenceapp.shared.ui.components.badges.RankBadge
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

private val PagerHeight = 360.dp
private val RowShape: Shape = RoundedCornerShape(18.dp)

private val Top1Brush: Brush = Brush.horizontalGradient(
    colors = listOf(
        LuxeGold.copy(alpha = 0.14f),
        LuxeGold.copy(alpha = 0.05f),
        LuxeGold.copy(alpha = 0.0f)
    )
)

private val Top2Brush: Brush = Brush.horizontalGradient(
    colors = listOf(
        SoftRose.copy(alpha = 0.14f),
        SoftRose.copy(alpha = 0.05f),
        SoftRose.copy(alpha = 0.0f)
    )
)

private val Top3Brush: Brush = Brush.horizontalGradient(
    colors = listOf(
        MutedTeal.copy(alpha = 0.14f),
        MutedTeal.copy(alpha = 0.05f),
        MutedTeal.copy(alpha = 0.0f)
    )
)

private fun topBrushFor(position: Int): Brush? = when (position) {
    1 -> Top1Brush
    2 -> Top2Brush
    3 -> Top3Brush
    else -> null
}

@Composable
fun SongsPagerSection(
    songs: List<SongSimple>,
    sourceKey: String,
    accent: Color,
    onOpenSong: (PlaybackOpenRequest) -> Unit,
    modifier: Modifier = Modifier,
    pageSize: Int = 5,
    showRank: Boolean = false
) {
    if (songs.isEmpty()) return

    val pages = remember(songs, pageSize) { songs.chunked(pageSize) }
    val queueItems = remember(songs) { songs.toQueueItems() }

    Column(modifier = modifier.fillMaxWidth()) {
        if (pages.size > 1) {
            val pagerState = rememberPagerState(pageCount = { pages.size })

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 10.dp,
                key = { pageIndex -> pageIndex },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PagerHeight)
            ) { pageIndex ->
                SongsPage(
                    songs = pages[pageIndex],
                    pageStartIndex = pageIndex * pageSize,
                    sourceKey = sourceKey,
                    queueItems = queueItems,
                    showRank = showRank,
                    onOpenSong = onOpenSong
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PagerDots(
                pageCount = pages.size,
                selected = pagerState.currentPage,
                accent = accent,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            SongsPage(
                songs = pages[0],
                pageStartIndex = 0,
                sourceKey = sourceKey,
                queueItems = queueItems,
                showRank = showRank,
                onOpenSong = onOpenSong,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun SongsPage(
    songs: List<SongSimple>,
    pageStartIndex: Int,
    sourceKey: String,
    queueItems: List<PlaybackQueueItem>,
    showRank: Boolean,
    onOpenSong: (PlaybackOpenRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        songs.forEachIndexed { index, song ->
            val globalPosition = pageStartIndex + index + 1
            val rowBrush = if (showRank) topBrushFor(globalPosition) else null

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RowShape)
                    .let { mod ->
                        if (rowBrush != null) mod.background(brush = rowBrush) else mod
                    }
                    .clickable {
                        onOpenSong(
                            PlaybackOpenRequest(
                                songLookup = song.detailLookup,
                                queue = queueItems,
                                startIndex = pageStartIndex + index,
                                sourceKey = sourceKey
                            )
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showRank) {
                    RankBadge(position = globalPosition)
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    CompactSongContent(
                        song = song,
                        showAddToPlaylistAction = true
                    )
                }
            }
        }
    }
}

@Composable
private fun PagerDots(
    pageCount: Int,
    selected: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == selected
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(
                        width = if (isSelected) 18.dp else 6.dp,
                        height = 6.dp
                    )
                    .background(
                        color = if (isSelected) accent.copy(alpha = 0.85f) else PureWhite.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
