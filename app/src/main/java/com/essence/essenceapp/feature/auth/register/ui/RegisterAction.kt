package com.essence.essenceapp.feature.auth.register.ui

sealed interface RegisterAction {
    data class UsernameChanged(val value: String): RegisterAction
    data class EmailChanged(val value: String): RegisterAction
    data class PasswordChanged(val value: String): RegisterAction
    data object Submit : RegisterAction
    data object ClearError : RegisterAction
}