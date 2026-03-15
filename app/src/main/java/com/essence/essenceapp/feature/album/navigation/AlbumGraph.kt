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

fun NavGraphBuilder.albumGraph(
    navController: NavController
) {
    navigation(
        route = AlbumGraphRoutes.ALBUM_GRAPH,
        startDestination = AlbumRoutes.ALBUM_DETAIL
    ) {
        composable(
            route = AlbumRoutes.ALBUM_DETAIL,
            arguments = listOf(
                navArgument(AlbumRoutes.ALBUM_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val albumId = args
                ?.takeIf { it.containsKey(AlbumRoutes.ALBUM_ID) }
                ?.getLong(AlbumRoutes.ALBUM_ID)

            if (albumId == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            AlbumDetailScreen(
                albumId = albumId,
                onBack = { navController.popBackStack() },
                onOpenSong = { songId -> navController.navigate(SongRoutes.detail(songId)) }
            )
        }
    }
}
