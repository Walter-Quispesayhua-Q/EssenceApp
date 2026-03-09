package com.essence.essenceapp.feature.auth.register.ui

sealed interface UsernameStatus {

    data object Idle : UsernameStatus

    data object Checking : UsernameStatus

    data object Available : UsernameStatus

    data class Unavailable(
        val message: String
    ) : UsernameStatus
}