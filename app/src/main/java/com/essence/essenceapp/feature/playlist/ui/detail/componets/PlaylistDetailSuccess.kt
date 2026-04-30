package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.PlaylistUtils
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailAction
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal

@Composable
internal fun PlaylistDetailSuccess(
    playlist: Playlist,
    songs: List<SongSimple>,
    isSongsLoading: Boolean,
    isLikeSubmitting: Boolean,
    onAction: (PlaylistDetailAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    isDeleting: Boolean = false,
    deleteError: String? = null,
    onDismissDeleteError: () -> Unit = {}
) {
    val bottomClearance = LocalBottomBarClearance.current
    val isSystem = PlaylistUtils.isSystemPlaylist(playlist.type)
    val canPlay = !isSongsLoading && songs.isNotEmpty()
    val firstSongLookup = songs.firstOrNull()?.detailLookup
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteError) {
        val message = deleteError ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = "Reintentar",
            withDismissAction = true
        )
        onDismissDeleteError()
        if (result == SnackbarResult.ActionPerformed) {
            onAction(PlaylistDetailAction.DeletePlaylist)
        }
    }

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
                PlaylistDetailHero(
                    playlist = playlist,
                    isSystem = isSystem,
                    isOwner = playlist.isOwner,
                    isLikeSubmitting = isLikeSubmitting,
                    canPlay = canPlay,
                    onBack = onBack,
                    onDelete = { onAction(PlaylistDetailAction.DeletePlaylist) },
                    onToggleLike = { onAction(PlaylistDetailAction.ToggleLike) },
                    onPlay = {
                        firstSongLookup?.let { onAction(PlaylistDetailAction.OpenSong(it)) }
                    },
                    onShuffle = {
                        if (songs.isNotEmpty()) {
                            val randomIndex = songs.indices.random()
                            onAction(PlaylistDetailAction.OpenSong(songs[randomIndex].detailLookup))
                        }
                    },
                    onEdit = { onAction(PlaylistDetailAction.EditPlaylist) },
                    onAddSongs = { onAction(PlaylistDetailAction.AddSongs) }
                )
            }

            item("stats") {
                Spacer(modifier = Modifier.height(18.dp))
                PlaylistStatsIsland(
                    playlist = playlist,
                    isSystem = isSystem,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item("songs_header") {
                Spacer(modifier = Modifier.height(24.dp))
                PlaylistSongsHeader(
                    totalSongs = playlist.totalSongs,
                    isSystem = isSystem,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item("songs_body") {
                when {
                    isSongsLoading -> PlaylistSongsLoadingState(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    songs.isEmpty() && isSystem -> PlaylistLikedSongsEmptyState(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    songs.isEmpty() -> PlaylistSongsEmptyState(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    else -> PlaylistSongsIsland(
                        songs = songs,
                        isPublic = playlist.isPublic,
                        isSystem = isSystem,
                        isOwner = playlist.isOwner,
                        onRemove = { songId ->
                            onAction(PlaylistDetailAction.RemoveSong(songId))
                        },
                        onSongClick = { lookup ->
                            onAction(PlaylistDetailAction.OpenSong(lookup))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isRefreshing,
            enter = fadeIn(animationSpec = tween(durationMillis = 600)),
            exit = fadeOut(animationSpec = tween(durationMillis = 800)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MutedTeal.copy(alpha = 0.55f),
                trackColor = Color.Transparent
            )
        }

        DeletingOverlay(visible = isDeleting)

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomClearance + 16.dp, start = 12.dp, end = 12.dp)
        )
    }
}
