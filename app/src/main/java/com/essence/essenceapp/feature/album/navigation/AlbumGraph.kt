package com.essence.essenceapp.feature.album.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.ui.AlbumDetailScreen
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.currentHlsMasterKey
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.albumGraph(
    navController: NavController,
    playbackController: PlaybackController,
    isLoggedIn: Boolean,
    onGuestPlayAttempt: () -> Unit
) {
    navigation(
        route = AlbumGraphRoutes.ALBUM_GRAPH,
        startDestination = AlbumRoutes.ALBUM_DETAIL
    ) {
        composable(
            route = AlbumRoutes.ALBUM_DETAIL,
            arguments = listOf(
                navArgument(AlbumRoutes.ALBUM_LOOKUP) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val albumLookup = backStackEntry.arguments?.getString(AlbumRoutes.ALBUM_LOOKUP)

            if (albumLookup.isNullOrBlank()) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            OffscreenSurface {
                AlbumDetailScreen(
                    albumLookup = albumLookup,
                    isLoggedIn = isLoggedIn,
                    onBack = { navController.popBackStack() },
                    onOpenSong = { request ->
                        if (!isLoggedIn) {
                            onGuestPlayAttempt()
                            return@AlbumDetailScreen
                        }

                        playbackController.dispatch(PlaybackAction.Open(request))
                        request.currentHlsMasterKey()?.let { hlsMasterKey ->
                            navController.navigate(SongRoutes.detail(hlsMasterKey))
                        }
                    }
                )
            }
        }
    }
}
