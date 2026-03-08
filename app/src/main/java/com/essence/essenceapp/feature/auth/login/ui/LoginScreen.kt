package com.essence.essenceapp.feature.auth.login.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.auth.login.ui.components.LoginContent
import com.essence.essenceapp.feature.auth.login.ui.components.LoginTopBar

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { LoginTopBar(title = "Iniciar Sesion") }
    ) {
        innerPadding ->
        LoginContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = viewModel::onAction
        )
    }
}