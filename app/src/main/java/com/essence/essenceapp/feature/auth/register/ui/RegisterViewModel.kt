package com.essence.essenceapp.feature.auth.register.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.auth.register.domain.model.RegisterRequest
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterEmailAlreadyUsedException
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterSubmissionException
import com.essence.essenceapp.feature.auth.register.domain.usecase.RegisterUseCase
import com.essence.essenceapp.feature.auth.register.domain.usecase.UsernameAvailableUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val usernameAvailableUseCase: UsernameAvailableUseCase
) : ViewModel() {

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
                it.copy(
                    form = it.form.copy(email = action.value),
                    emailError = null
                )
            }

            is RegisterAction.PasswordChanged -> updateEditing {
                it.copy(form = it.form.copy(password = action.value))
            }

            is RegisterAction.Submit -> submitRegister()

            is RegisterAction.ClearError -> updateEditing {
                it.copy(
                    emailError = null,
                    errorMessage = null
                )
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
            updateEditing {
                it.copy(
                    isSubmitting = true,
                    emailError = null,
                    errorMessage = null
                )
            }

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
                    when (error) {
                        is RegisterEmailAlreadyUsedException -> it.copy(
                            isSubmitting = false,
                            emailError = error.message,
                            errorMessage = null
                        )

                        else -> it.copy(
                            isSubmitting = false,
                            emailError = null,
                            errorMessage = error.toRegisterErrorMessage()
                        )
                    }
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
                    it.copy(
                        usernameStatus = if (isAvailable) {
                            UsernameStatus.Available
                        } else {
                            UsernameStatus.Unavailable("Ya está en uso")
                        }
                    )
                }
            }
            result.onFailure {
                updateEditing { it.copy(usernameStatus = UsernameStatus.Idle) }
            }
        }
    }

    private fun Throwable.toRegisterErrorMessage(): String = when (this) {
        is RegisterSubmissionException -> message ?: "No se pudo completar el registro."
        is IOException -> toUserMessage()
        else -> message?.takeIf { it.isNotBlank() } ?: toUserMessage()
    }
}
