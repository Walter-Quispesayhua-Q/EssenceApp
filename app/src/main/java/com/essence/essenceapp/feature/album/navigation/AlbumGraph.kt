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
                navArgument(AlbumRoutes.ALBUM_LOOKUP) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val albumLookup = backStackEntry.arguments?.getString(AlbumRoutes.ALBUM_LOOKUP)

            if (albumLookup.isNullOrBlank()) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            AlbumDetailScreen(
                albumLookup = albumLookup,
                onBack = { navController.popBackStack() },
                onOpenSong = { songLookup ->
                    navController.navigate(SongRoutes.detail(songLookup))
                }
            )
        }
    }
}