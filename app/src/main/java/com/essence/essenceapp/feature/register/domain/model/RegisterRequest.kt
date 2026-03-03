package com.essence.essenceapp.feature.register.domain.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
