package com.essence.essenceapp.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.currentHlsMasterKey
import com.essence.essenceapp.feature.search.ui.SearchScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.searchGraph(
    navController: NavController,
    playbackController: PlaybackController,
    isLoggedIn: Boolean,
    onGuestPlayAttempt: () -> Unit
) {
    navigation(
        route = SearchGraphRoutes.SEARCH_GRAPH,
        startDestination = SearchRoutes.SEARCH
    ) {
        composable(route = SearchRoutes.SEARCH) {
            OffscreenSurface {
                SearchScreen(
                    isLoggedIn = isLoggedIn,
                    onOpenSong = { request ->
                        if (!isLoggedIn) {
                            onGuestPlayAttempt()
                            return@SearchScreen
                        }

                        playbackController.dispatch(PlaybackAction.Open(request))
                        request.currentHlsMasterKey()?.let { hlsMasterKey ->
                            navController.navigate(SongRoutes.detail(hlsMasterKey))
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