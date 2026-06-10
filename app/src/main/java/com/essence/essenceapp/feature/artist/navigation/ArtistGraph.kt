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
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.currentHlsMasterKey
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.artistGraph(
    navController: NavController,
    playbackController: PlaybackController,
    isLoggedIn: Boolean,
    onGuestPlayAttempt: () -> Unit
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
                    isLoggedIn = isLoggedIn,
                    onBack = { navController.popBackStack() },
                    onOpenSong = { request ->
                        if (!isLoggedIn) {
                            onGuestPlayAttempt()
                            return@ArtistDetailScreen
                        }

                        playbackController.dispatch(PlaybackAction.Open(request))
                        request.currentHlsMasterKey()?.let { hlsMasterKey ->
                            navController.navigate(SongRoutes.detail(hlsMasterKey))
                        }
                    },
                    onOpenAlbum = { albumLookup ->
                        navController.navigate(AlbumRoutes.detail(albumLookup))
                    }
                )
            }
        }
    }
}
