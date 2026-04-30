package com.essence.essenceapp.feature.album.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.ui.AlbumDetailScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.albumGraph(
    navController: NavController,
    playbackManager: PlaybackManager
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
                    onBack = { navController.popBackStack() },
                    onOpenSong = { request ->
                        playbackManager.setQueueFromItems(
                            items = request.queue,
                            startIndex = request.startIndex,
                            sourceKey = request.sourceKey
                        )
                        navController.navigate(SongRoutes.detail(request.songLookup))
                    }
                )
            }
        }
    }
}