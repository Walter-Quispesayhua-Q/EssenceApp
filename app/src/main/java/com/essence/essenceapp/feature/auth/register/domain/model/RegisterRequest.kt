package com.essence.essenceapp.feature.auth.register.domain.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
