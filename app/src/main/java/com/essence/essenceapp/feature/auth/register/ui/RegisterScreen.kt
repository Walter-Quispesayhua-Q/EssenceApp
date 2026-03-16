package com.essence.essenceapp.feature.auth.register.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.auth.register.ui.components.RegisterContent

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    LaunchedEffect(state) {
        if (state is RegisterUiState.Success) onRegisterSuccess()
    }

    RegisterContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onNavigateToLogin = onNavigateToLogin
    )
}