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
            navArgument(SongRoutes.SONG_ID) { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val songId = backStackEntry.arguments?.getLong(SongRoutes.SONG_ID)
        if (songId == null) {
            LaunchedEffect(Unit) { navController.popBackStack() }
            return@composable
        }

        if (!isLoggedIn) {
            LaunchedEffect(songId) {
                navController.popBackStack()
                onRequireAuth()
            }
            return@composable
        }

        SongDetailScreen(
            songId = songId,
            onBack = { navController.popBackStack() },
            onOpenArtist = { artistId ->
                navController.navigate(ArtistRoutes.detail(artistId))
            },
            onOpenAlbum = { albumId ->
                navController.navigate(AlbumRoutes.detail(albumId))
            }
        )
    }
}