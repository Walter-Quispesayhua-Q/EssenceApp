package com.essence.essenceapp.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.search.ui.SearchScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes

fun NavGraphBuilder.searchGraph(
    navController: NavController,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    navigation(
        route = SearchGraphRoutes.SEARCH_GRAPH,
        startDestination = SearchRoutes.SEARCH
    ) {
        composable(route = SearchRoutes.SEARCH) {
            SearchScreen(
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