package com.essence.essenceapp.feature.song.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.song.ui.SongDetailScreen

fun NavGraphBuilder.songGraph(
    navController: NavController,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    composable(
        route = SongRoutes.SONG_DETAIL,
        arguments = listOf(
            navArgument(SongRoutes.HLS_MASTER_KEY) { type = NavType.StringType }
        ),
        enterTransition = { songDetailEnter() },
        exitTransition = { songDetailExit() },
        popEnterTransition = { songDetailPopEnter() },
        popExitTransition = { songDetailPopExit() }
    ) { backStackEntry ->
        if (!isLoggedIn) {
            LaunchedEffect(Unit) {
                navController.popBackStack()
                onRequireAuth()
            }
            return@composable
        }

        val hlsMasterKey = backStackEntry.arguments?.getString(SongRoutes.HLS_MASTER_KEY)
        if (hlsMasterKey.isNullOrBlank()) {
            LaunchedEffect(Unit) { navController.popBackStack() }
            return@composable
        }

        SongDetailScreen(
            hlsMasterKey = hlsMasterKey,
            onBack = { navController.popBackStack() },
            onOpenArtist = { artistLookup ->
                navController.navigate(ArtistRoutes.detail(artistLookup))
            },
            onOpenAlbum = { albumLookup ->
                navController.navigate(AlbumRoutes.detail(albumLookup))
            }
        )
    }
}