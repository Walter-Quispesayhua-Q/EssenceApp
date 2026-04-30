package com.essence.essenceapp.feature.artist.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.ui.ArtistDetailScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.artistGraph(
    navController: NavController,
    playbackManager: PlaybackManager
) {
    navigation(
        route = ArtistGraphRoutes.ARTIST_GRAPH,
        startDestination = ArtistRoutes.ARTIST_DETAIL
    ) {
        composable(
            route = ArtistRoutes.ARTIST_DETAIL,
            arguments = listOf(
                navArgument(ArtistRoutes.ARTIST_LOOKUP) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val artistLookup = backStackEntry.arguments?.getString(ArtistRoutes.ARTIST_LOOKUP)

            if (artistLookup.isNullOrBlank()) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            OffscreenSurface {
                ArtistDetailScreen(
                    artistLookup = artistLookup,
                    onBack = { navController.popBackStack() },
                    onOpenSong = { request ->
                        playbackManager.setQueueFromItems(
                            items = request.queue,
                            startIndex = request.startIndex,
                            sourceKey = request.sourceKey
                        )
                        navController.navigate(SongRoutes.detail(request.songLookup))
                    },
                    onOpenAlbum = { albumLookup ->
                        navController.navigate(AlbumRoutes.detail(albumLookup))
                    }
                )
            }
        }
    }
}