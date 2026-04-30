package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.artist.ui.ArtistDetailAction
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun ArtistDetailSuccess(
    artist: Artist,
    isLikeSubmitting: Boolean,
    onAction: (ArtistDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val songs = artist.songs.orEmpty()
    val albums = artist.albums.orEmpty()
    val totalPlays = songs.sumOf { it.totalPlays ?: 0L }
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
                ArtistDetailHero(
                    artist = artist,
                    isLikeSubmitting = isLikeSubmitting,
                    onBack = { onAction(ArtistDetailAction.Back) },
                    onToggleLike = { onAction(ArtistDetailAction.ToggleLike) }
                )
            }

            item("stats") {
                Spacer(modifier = Modifier.height(16.dp))
                ArtistStatsIsland(
                    songsCount = songs.size,
                    albumsCount = albums.size,
                    totalPlays = totalPlays,
                    country = artist.country,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item("actions") {
                Spacer(modifier = Modifier.height(16.dp))
                ArtistActionsRow(
                    enabled = songs.isNotEmpty(),
                    onPlay = {
                        firstLookup?.let { onAction(ArtistDetailAction.OpenSong(it)) }
                    },
                    onShuffle = {
                        songs.shuffled().firstOrNull()?.let {
                            onAction(ArtistDetailAction.OpenSong(it.detailLookup))
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item("songs_header") {
                Spacer(modifier = Modifier.height(28.dp))
                ArtistSectionHeader(
                    title = "Canciones populares",
                    count = songs.size,
                    accent = SoftRose,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item("songs_body") {
                if (songs.isEmpty()) {
                    ArtistEmptyState(
                        icon = Icons.Outlined.MusicNote,
                        title = "Sin canciones disponibles",
                        message = "Este artista todavia no tiene canciones publicadas.",
                        accent = SoftRose,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    ArtistTracksIsland(
                        songs = songs,
                        onOpenSong = { onAction(ArtistDetailAction.OpenSong(it)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item("discography_header") {
                Spacer(modifier = Modifier.height(28.dp))
                ArtistSectionHeader(
                    title = "Discografia",
                    count = albums.size,
                    accent = LuxeGold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item("discography_body") {
                if (albums.isEmpty()) {
                    ArtistEmptyState(
                        icon = Icons.Outlined.Album,
                        title = "Sin albumes disponibles",
                        message = "Aun no hay discografia publicada para este artista.",
                        accent = LuxeGold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    ArtistDiscographyRow(
                        albums = albums,
                        onOpenAlbum = { onAction(ArtistDetailAction.OpenAlbum(it)) }
                    )
                }
            }
        }
    }
}
