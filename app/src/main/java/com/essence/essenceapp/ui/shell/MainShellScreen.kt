package com.essence.essenceapp.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.essence.essenceapp.ui.shell.components.AppBottomBar
import com.essence.essenceapp.ui.shell.components.MainTabsNavHost
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
fun MainShellScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier
            .fillMaxSize()
            .bottomGlobalFade()
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                AppBottomBar(
                    selectedGraphRoute = currentRoute,
                    onDestinationSelected = { destination ->
                        navController.navigate(destination.graphRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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
                MainTabsNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun Modifier.bottomGlobalFade(): Modifier = drawWithContent {
    drawContent()

    val fadeHeightPx = 190.dp.toPx()
    val startY = size.height - fadeHeightPx

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                androidx.compose.ui.graphics.Color.Transparent,
                GraphiteSurface.copy(alpha = 0.20f),
                MidnightBlack.copy(alpha = 0.85f)
            ),
            startY = startY,
            endY = size.height
        )
    )
}
