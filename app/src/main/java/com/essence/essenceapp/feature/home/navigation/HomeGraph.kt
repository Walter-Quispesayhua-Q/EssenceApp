package com.essence.essenceapp.feature.home.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.home.ui.HomeScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.ShellEmphasizedDecelerate

private const val HOME_FAST_ENTER_MS = 10

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    playbackManager: PlaybackManager,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    navigation(
        route = HomeGraphRoutes.HOME_GRAPH,
        startDestination = HomeRoutes.HOME
    ) {
        composable(
            route = HomeRoutes.HOME,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = HOME_FAST_ENTER_MS,
                        easing = ShellEmphasizedDecelerate
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = HOME_FAST_ENTER_MS,
                        easing = ShellEmphasizedDecelerate
                    )
                )
            }
        ) {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                onLoginClick = onRequireAuth,
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