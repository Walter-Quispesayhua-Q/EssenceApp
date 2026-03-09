package com.essence.essenceapp.feature.auth.register.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.auth.register.domain.model.RegisterRequest
import com.essence.essenceapp.feature.auth.register.domain.usecase.RegisterUseCase
import com.essence.essenceapp.feature.auth.register.domain.usecase.UsernameAvailableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val usernameAvailableUseCase: UsernameAvailableUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Editing())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UsernameChanged -> {
                updateEditing {
                    it.copy(form = it.form.copy(username = action.value))
                }
                if (action.value.length >= 3) {
                    checkUsername(action.value)
                }
            }
            is RegisterAction.EmailChanged -> updateEditing {
                it.copy(form = it.form.copy(email = action.value))
            }
            is RegisterAction.PasswordChanged -> updateEditing {
                it.copy(form = it.form.copy(password = action.value))
            }
            is RegisterAction.Submit -> submitRegister()
            is RegisterAction.ClearError -> updateEditing {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun updateEditing(transform: (RegisterUiState.Editing) -> RegisterUiState.Editing) {
        val current = _uiState.value
        if (current is RegisterUiState.Editing) {
            _uiState.value = transform(current)
        }
    }

    private fun submitRegister() {
        val current = _uiState.value
        if (current !is RegisterUiState.Editing) return
        viewModelScope.launch {
            updateEditing { it.copy(isSubmitting = true) }
            val result = registerUseCase(
                RegisterRequest(
                    username = current.form.username,
                    email = current.form.email,
                    password = current.form.password
                )
            )
            result.onSuccess {
                _uiState.value = RegisterUiState.Success
            }
            result.onFailure { error ->
                updateEditing {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun checkUsername(username: String) {
        viewModelScope.launch {
            updateEditing { it.copy(usernameStatus = UsernameStatus.Checking) }
            val result = usernameAvailableUseCase(username)
            result.onSuccess { isAvailable ->
                updateEditing {
                    it.copy(usernameStatus = if (isAvailable)
                        UsernameStatus.Available
                    else
                        UsernameStatus.Unavailable("Ya está en uso")
                    )
                }
            }
            result.onFailure {
                updateEditing { it.copy(usernameStatus = UsernameStatus.Idle) }
            }
        }
    }
}