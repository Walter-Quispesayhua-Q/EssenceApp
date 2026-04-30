package com.essence.essenceapp.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.auth.login.ui.LoginScreen
import com.essence.essenceapp.feature.auth.register.ui.RegisterScreen
import com.essence.essenceapp.ui.shell.shellEnterTransition
import com.essence.essenceapp.ui.shell.shellExitTransition

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onExitAuth: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    navigation(
        route = AuthGraphRoutes.AUTH_GRAPH,
        startDestination = AuthRoutes.AUTH_LOGIN
    ) {
        composable(
            route = AuthRoutes.AUTH_LOGIN,
            enterTransition = { shellEnterTransition },
            exitTransition = { shellExitTransition },
            popEnterTransition = { shellEnterTransition },
            popExitTransition = { shellExitTransition }
        ) {
            LoginScreen(
                onBack = onExitAuth,
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = {
                    navController.navigate(AuthRoutes.AUTH_REGISTER) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AuthRoutes.AUTH_REGISTER,
            enterTransition = { shellEnterTransition },
            exitTransition = { shellExitTransition },
            popEnterTransition = { shellEnterTransition },
            popExitTransition = { shellExitTransition }
        ) {
            RegisterScreen(
                onBack = onExitAuth,
                onNavigateToLogin = {
                    navController.navigate(AuthRoutes.AUTH_LOGIN) {
                        popUpTo(AuthGraphRoutes.AUTH_GRAPH)
                        launchSingleTop = true
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(AuthRoutes.AUTH_LOGIN) {
                        popUpTo(AuthGraphRoutes.AUTH_GRAPH)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}