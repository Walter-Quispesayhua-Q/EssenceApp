package com.essence.essenceapp.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.search.ui.SearchScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.searchGraph(
    navController: NavController,
    playbackManager: PlaybackManager,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    navigation(
        route = SearchGraphRoutes.SEARCH_GRAPH,
        startDestination = SearchRoutes.SEARCH
    ) {
        composable(route = SearchRoutes.SEARCH) {
            OffscreenSurface {
                SearchScreen(
                    onOpenSong = { request ->
                        if (isLoggedIn) {
                            playbackManager.setQueueFromItems(
                                items = request.queue,
                                startIndex = request.startIndex,
                                sourceKey = request.sourceKey
                            )
                            navController.navigate(SongRoutes.detail(request.songLookup))
                        } else {
                            onRequireAuth()
                        }
                    },
                    onOpenAlbum = { albumLookup ->
                        navController.navigate(AlbumRoutes.detail(albumLookup))
                    },
                    onOpenArtist = { artistLookup ->
                        navController.navigate(ArtistRoutes.detail(artistLookup))
                    }
                )
            }
        }
    }
}