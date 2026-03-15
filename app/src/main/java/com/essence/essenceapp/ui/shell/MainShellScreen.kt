package com.essence.essenceapp.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import com.essence.essenceapp.ui.shell.components.AppBottomBar
import com.essence.essenceapp.ui.shell.components.MainTabsNavHost
import com.essence.essenceapp.ui.shell.model.TopLevelDestination
import com.essence.essenceapp.ui.shell.model.TopLevelDestinations
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
fun MainShellScreen(
    modifier: Modifier = Modifier,
    onRequireAuth: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val shellViewModel: MainShellViewModel = hiltViewModel()
    val isLoggedIn by shellViewModel.isLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        shellViewModel.refreshAuthState()
    }

    val bottomBarItems = TopLevelDestinations.itemsFor(isLoggedIn)
    val selectedTopLevelGraphRoute = resolveSelectedTopLevelGraphRoute(
        currentDestination = navBackStackEntry?.destination,
        items = bottomBarItems
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .bottomGlobalFade()
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
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
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
            ) {
                CompositionLocalProvider(
                    LocalBottomBarClearance provides FloatingBottomBarHeight
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