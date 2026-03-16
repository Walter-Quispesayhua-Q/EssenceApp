package com.essence.essenceapp.feature.auth.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.network.storage.TokenManager
import com.essence.essenceapp.feature.auth.login.domain.model.Login
import com.essence.essenceapp.feature.auth.login.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
): ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var email = ""
    private var password = ""

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> email = action.value
            is LoginAction.PasswordChanged -> password = action.value
            is LoginAction.Submit -> submitLogin()
            is LoginAction.ClearError -> _uiState.value = LoginUiState.Idle
        }
    }

    private fun submitLogin() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = loginUseCase(Login(email, password))
            result.onSuccess { token ->
                tokenManager.saveTokenAndUserId(token.token)
                _uiState.value = LoginUiState.Success
            }
            result.onFailure { error ->
                _uiState.value = LoginUiState.Error(
                    message = error.message ?: "Error desconocido"
                )
            }
        }
    }
}