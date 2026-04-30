package com.essence.essenceapp.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.profile.ui.ProfileScreen
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.profileGraph() {
    navigation(
        route = ProfileGraphRoutes.PROFILE_GRAPH,
        startDestination = ProfileRoutes.PROFILE
    ) {
        composable(route = ProfileRoutes.PROFILE) {
            OffscreenSurface {
                ProfileScreen()
            }
        }
    }
}