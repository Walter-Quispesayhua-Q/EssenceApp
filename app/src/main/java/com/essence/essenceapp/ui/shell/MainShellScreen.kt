package com.essence.essenceapp.ui.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.ui.miniplayer.MiniPlayer
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.ui.shell.components.AppBottomBar
import com.essence.essenceapp.ui.shell.components.MainTabsNavHost
import com.essence.essenceapp.ui.shell.model.TopLevelDestination
import com.essence.essenceapp.ui.shell.model.TopLevelDestinations
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MainShellScreen(
    playbackController: PlaybackController,
    modifier: Modifier = Modifier,
    onRequireAuth: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val shellViewModel: MainShellViewModel = hiltViewModel()
    val isLoggedIn by shellViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val nowPlaying by playbackController.nowPlaying.collectAsStateWithLifecycle()
    val playback by playbackController.uiState.collectAsStateWithLifecycle()
    val hasMiniPlayer = nowPlaying != null

    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()
    val songDetailBaseRoute = SongRoutes.SONG_DETAIL.substringBefore("?")
    val isSongDetailRoute = currentRoute.startsWith(songDetailBaseRoute)

    val shouldShowBottomBar = !isSongDetailRoute
    val shouldShowMiniPlayer = hasMiniPlayer && shouldShowBottomBar

    LaunchedEffect(Unit) {
        shellViewModel.sessionExpiredEvent.collectLatest {
            playbackController.dispatch(PlaybackAction.Stop)
            launch {
                snackbarHostState.showSnackbar(
                    message = "Tu sesion ha expirado. Vuelve a iniciar sesion."
                )
            }
            onRequireAuth()
        }
    }

    LaunchedEffect(Unit) {
        shellViewModel.authRequiredEvent.collectLatest {
            launch {
                snackbarHostState.showSnackbar(
                    message = "Inicia sesion para continuar."
                )
            }
            onRequireAuth()
        }
    }

    LaunchedEffect(Unit) {
        shellViewModel.guestPlayEvent.collectLatest {
            launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Inicia sesion para reproducir musica",
                    actionLabel = "Iniciar sesion",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onRequireAuth()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        shellViewModel.connectivityEvent.collectLatest {
            launch {
                snackbarHostState.showSnackbar(
                    message = "Conexion inestable, reintentando...",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        shellViewModel.unavailableSkippingEvent.collectLatest { title ->
            val routeAtEmission = navController.currentDestination?.route.orEmpty()
            val isOnSongDetail = routeAtEmission.startsWith(songDetailBaseRoute)
            if (isOnSongDetail) return@collectLatest

            launch {
                snackbarHostState.showSnackbar(
                    message = "$title no disponible, saltando a la siguiente...",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    val bottomBarItems = TopLevelDestinations.itemsFor(isLoggedIn)
    val selectedTopLevelGraphRoute = resolveSelectedTopLevelGraphRoute(
        currentDestination = navBackStackEntry?.destination,
        items = bottomBarItems
    )

    val bottomClearance = when {
        !shouldShowBottomBar -> 0.dp
        shouldShowMiniPlayer -> FloatingBottomBarWithMiniPlayerHeight
        else -> FloatingBottomBarHeight
    }

    Box(
        modifier = if (shouldShowBottomBar) {
            modifier
                .fillMaxSize()
                .bottomGlobalFade(fadeHeight = bottomClearance + 16.dp)
        } else {
            modifier.fillMaxSize()
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = shouldShowBottomBar,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(
                            durationMillis = SHELL_BOTTOM_BAR_ENTER_DURATION_MS,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = SHELL_BOTTOM_BAR_ENTER_DURATION_MS,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(
                            durationMillis = SHELL_BOTTOM_BAR_EXIT_DURATION_MS,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis = SHELL_BOTTOM_BAR_EXIT_DURATION_MS,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {
                    Column {
                        AnimatedVisibility(
                            visible = shouldShowMiniPlayer,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            nowPlaying?.let { info ->
                                MiniPlayer(
                                    nowPlaying = info,
                                    playback = playback,
                                    onTogglePlay = {
                                        playbackController.dispatch(
                                            if (playback.isPlaying || playback.isBuffering) {
                                                PlaybackAction.Pause
                                            } else {
                                                PlaybackAction.Play
                                            }
                                        )
                                    },
                                    onNext = {
                                        playbackController.dispatch(PlaybackAction.Next)
                                    },
                                    onPrevious = {
                                        playbackController.dispatch(PlaybackAction.Previous)
                                    },
                                    onDismiss = {
                                        playbackController.dispatch(PlaybackAction.Stop)
                                    },
                                    onTap = {
                                        navController.navigate(
                                            SongRoutes.detail(info.item.hlsMasterKey)
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }

                        AppBottomBar(
                            selectedGraphRoute = selectedTopLevelGraphRoute,
                            items = bottomBarItems,
                            onDestinationSelected = { destination ->
                                if (destination.requiresAuth && !isLoggedIn) {
                                    onRequireAuth()
                                    return@AppBottomBar
                                }

                                val isReselect =
                                    selectedTopLevelGraphRoute == destination.graphRoute

                                if (isReselect) {
                                    navController.popBackStack(
                                        route = destination.graphRoute,
                                        inclusive = false
                                    )
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
                        playbackController = playbackController,
                        isLoggedIn = isLoggedIn,
                        onRequireAuth = onRequireAuth,
                        onGuestPlayAttempt = shellViewModel::notifyGuestPlayAttempt
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

private fun Modifier.bottomGlobalFade(fadeHeight: Dp): Modifier = drawWithContent {
    drawContent()

    val fadeHeightPx = fadeHeight.toPx()
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