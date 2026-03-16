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
            navArgument(SongRoutes.SONG_LOOKUP) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val songLookup = backStackEntry.arguments?.getString(SongRoutes.SONG_LOOKUP)
        if (songLookup.isNullOrBlank()) {
            LaunchedEffect(Unit) { navController.popBackStack() }
            return@composable
        }

        if (!isLoggedIn) {
            LaunchedEffect(songLookup) {
                navController.popBackStack()
                onRequireAuth()
            }
            return@composable
        }

        SongDetailScreen(
            songLookup = songLookup,
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