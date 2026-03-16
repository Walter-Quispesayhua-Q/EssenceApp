package com.essence.essenceapp.feature.auth.login.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.auth.login.ui.components.LoginContent

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    LaunchedEffect(state) {
        if (state is LoginUiState.Success) onLoginSuccess()
    }

    LoginContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onNavigateToRegister = onNavigateToRegister
    )
}