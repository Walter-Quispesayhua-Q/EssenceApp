package com.essence.essenceapp.feature.register.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestApiDTO(
    val username: String,
    val email: String,
    val password: String
)
