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

fun NavGraphBuilder.artistGraph(
    navController: NavController
) {
    navigation(
        route = ArtistGraphRoutes.ARTIST_GRAPH,
        startDestination = ArtistRoutes.ARTIST_DETAIL
    ) {
        composable(
            route = ArtistRoutes.ARTIST_DETAIL,
            arguments = listOf(
                navArgument(ArtistRoutes.ARTIST_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val artistId = args
                ?.takeIf { it.containsKey(ArtistRoutes.ARTIST_ID) }
                ?.getLong(ArtistRoutes.ARTIST_ID)

            if (artistId == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            ArtistDetailScreen(
                artistId = artistId,
                onBack = { navController.popBackStack() },
                onOpenSong = { songId -> navController.navigate(SongRoutes.detail(songId)) },
                onOpenAlbum = { albumId -> navController.navigate(AlbumRoutes.detail(albumId)) }
            )
        }
    }
}
