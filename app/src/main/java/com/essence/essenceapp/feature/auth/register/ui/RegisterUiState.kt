package com.essence.essenceapp.feature.auth.register.ui


sealed interface RegisterUiState {
    data object Idle : RegisterUiState

    data class Editing(
        val form: RegisterFormState = RegisterFormState(),
        val usernameStatus: UsernameStatus = UsernameStatus.Idle,
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    ) : RegisterUiState {
        val canSubmit: Boolean
            get() = form.isValid &&
                    !isSubmitting &&
                    usernameStatus is UsernameStatus.Available
    }

    data object Success : RegisterUiState
}