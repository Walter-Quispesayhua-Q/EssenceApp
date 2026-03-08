package com.essence.essenceapp.feature.auth.login.ui

sealed interface LoginAction {
    data class EmailChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data object Submit : LoginAction
    data object ClearError : LoginAction
}