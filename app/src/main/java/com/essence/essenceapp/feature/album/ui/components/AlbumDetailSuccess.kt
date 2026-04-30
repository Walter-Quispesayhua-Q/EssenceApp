package com.essence.essenceapp.feature.album.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.album.ui.AlbumDetailAction
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
internal fun AlbumDetailSuccess(
    album: Album,
    isLikeSubmitting: Boolean,
    onAction: (AlbumDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val songs = album.songs.orEmpty()
    val totalPlays = songs.sumOf { it.totalPlays ?: 0L }
    val totalDurationMs = songs.sumOf { it.durationMs.toLong() }
    val bottomClearance = LocalBottomBarClearance.current
    val firstLookup = songs.firstOrNull()?.detailLookup

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomClearance + 24.dp)
        ) {
            item("hero") {
                AlbumDetailHero(
                    album = album,
                    songs = songs,
                    isLikeSubmitting = isLikeSubmitting,
                    onBack = { onAction(AlbumDetailAction.Back) },
                    onToggleLike = { onAction(AlbumDetailAction.ToggleLike) }
                )
            }

            item("stats") {
                Spacer(modifier = Modifier.height(16.dp))
                AlbumStatsIsland(
                    songsCount = songs.size,
                    totalPlays = totalPlays,
                    totalDurationMs = totalDurationMs,
                    releaseYear = album.releaseDate?.year,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item("actions") {
                Spacer(modifier = Modifier.height(16.dp))
                AlbumActionsRow(
                    enabled = songs.isNotEmpty(),
                    onPlay = {
                        firstLookup?.let { onAction(AlbumDetailAction.OpenSong(it)) }
                    },
                    onShuffle = {
                        songs.shuffled().firstOrNull()?.let {
                            onAction(AlbumDetailAction.OpenSong(it.detailLookup))
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item("tracks_header") {
                Spacer(modifier = Modifier.height(28.dp))
                AlbumTracksHeader(
                    count = songs.size,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item("tracks_body") {
                if (songs.isEmpty()) {
                    AlbumTracksEmptyState(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    AlbumTracksIsland(
                        songs = songs,
                        onOpenSong = { onAction(AlbumDetailAction.OpenSong(it)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
