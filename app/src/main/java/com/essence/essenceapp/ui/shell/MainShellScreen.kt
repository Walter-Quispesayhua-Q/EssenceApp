package com.essence.essenceapp.ui.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.feature.song.ui.manager.PlaybackManager
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import com.essence.essenceapp.ui.shell.components.AppBottomBar
import com.essence.essenceapp.ui.shell.components.MainTabsNavHost
import com.essence.essenceapp.ui.shell.components.MiniPlayer
import com.essence.essenceapp.ui.shell.model.TopLevelDestination
import com.essence.essenceapp.ui.shell.model.TopLevelDestinations
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
fun MainShellScreen(
    modifier: Modifier = Modifier,
    playbackManager: PlaybackManager,
    onRequireAuth: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val shellViewModel: MainShellViewModel = hiltViewModel()
    val isLoggedIn by shellViewModel.isLoggedIn.collectAsStateWithLifecycle()

    val nowPlaying by playbackManager.nowPlaying.collectAsStateWithLifecycle()
    val playback by playbackManager.uiState.collectAsStateWithLifecycle()
    val hasMiniPlayer = nowPlaying != null

    LaunchedEffect(Unit) {
        shellViewModel.refreshAuthState()
    }

    val bottomBarItems = TopLevelDestinations.itemsFor(isLoggedIn)
    val selectedTopLevelGraphRoute = resolveSelectedTopLevelGraphRoute(
        currentDestination = navBackStackEntry?.destination,
        items = bottomBarItems
    )

    val bottomClearance = if (hasMiniPlayer) FloatingBottomBarWithMiniPlayerHeight
    else FloatingBottomBarHeight

    Box(
        modifier = modifier
            .fillMaxSize()
            .bottomGlobalFade()
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                Column {
                    AnimatedVisibility(
                        visible = hasMiniPlayer,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        nowPlaying?.let { info ->
                            MiniPlayer(
                                nowPlaying = info,
                                playback = playback,
                                onTogglePlay = {
                                    playbackManager.onAction(
                                        if (playback.isPlaying) SongDetailManagerAction.Pause
                                        else SongDetailManagerAction.Play
                                    )
                                },
                                onDismiss = {
                                    playbackManager.clearNowPlaying()
                                },
                                onTap = {
                                    navController.navigate(SongRoutes.detail(info.songLookup))
                                },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 8.dp)                            )
                        }
                    }

                    AppBottomBar(
                        selectedGraphRoute = selectedTopLevelGraphRoute,
                        items = bottomBarItems,
                        onDestinationSelected = { destination ->
                            if (destination.requiresAuth && !isLoggedIn) {
                                onRequireAuth()
                            } else {
                                navController.navigate(destination.graphRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
            ) {
                CompositionLocalProvider(
                    LocalBottomBarClearance provides bottomClearance
                ) {
                    MainTabsNavHost(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                        isLoggedIn = isLoggedIn,
                        onRequireAuth = onRequireAuth
                    )
                }
            }
        }
    }
}

private fun resolveSelectedTopLevelGraphRoute(
    currentDestination: NavDestination?,
    items: List<TopLevelDestination>
): String? {
    return items
        .firstOrNull { destination ->
            currentDestination?.isInGraphRoute(destination.graphRoute) == true
        }
        ?.graphRoute
}

private fun NavDestination.isInGraphRoute(graphRoute: String): Boolean {
    var node: NavDestination? = this
    while (node != null) {
        if (node.route == graphRoute) return true
        node = node.parent
    }
    return false
}

private fun Modifier.bottomGlobalFade(): Modifier = drawWithContent {
    drawContent()

    val fadeHeightPx = 190.dp.toPx()
    val startY = size.height - fadeHeightPx

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                GraphiteSurface.copy(alpha = 0.20f),
                MidnightBlack.copy(alpha = 0.85f)
            ),
            startY = startY,
            endY = size.height
        )
    )
}