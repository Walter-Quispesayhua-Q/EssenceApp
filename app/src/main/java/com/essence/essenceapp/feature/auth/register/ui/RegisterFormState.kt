package com.essence.essenceapp.feature.auth.register.ui

data class RegisterFormState(
    val username: String = "",
    val email: String = "",
    val password: String = ""
) {
    val isValid: Boolean
        get() = username.isNotBlank() &&
                email.isNotBlank() &&
                password.length >= 6
}
