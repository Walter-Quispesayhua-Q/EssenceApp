package com.essence.essenceapp.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.home.ui.HomeScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    navigation(
        route = HomeGraphRoutes.HOME_GRAPH,
        startDestination = HomeRoutes.HOME
    ) {
        composable(HomeRoutes.HOME) {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                onLoginClick = onRequireAuth,
                onOpenSong = { songId ->
                    if (isLoggedIn) {
                        navController.navigate(SongRoutes.detail(songId))
                    } else {
                        onRequireAuth()
                    }
                },
                onOpenAlbum = { albumId ->
                    navController.navigate(AlbumRoutes.detail(albumId))
                },
                onOpenArtist = { artistId ->
                    navController.navigate(ArtistRoutes.detail(artistId))
                }
            )
        }
    }
}