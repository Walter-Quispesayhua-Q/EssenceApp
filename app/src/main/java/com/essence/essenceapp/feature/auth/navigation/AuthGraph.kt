package com.essence.essenceapp.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.auth.login.ui.LoginScreen
import com.essence.essenceapp.feature.auth.register.ui.RegisterScreen

fun NavGraphBuilder.authGraph() {
    navigation(
        route = AuthGraphRoutes.AUTH_GRAPH,
        startDestination = AuthRoutes.AUTH_LOGIN
    ) {
        composable(route = AuthRoutes.AUTH_REGISTER) {
            RegisterScreen()
        }
        composable(route = AuthRoutes.AUTH_LOGIN) {
            LoginScreen()
        }
    }
}