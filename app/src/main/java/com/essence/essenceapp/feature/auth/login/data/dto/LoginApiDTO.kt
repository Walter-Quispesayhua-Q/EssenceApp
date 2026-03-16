package com.essence.essenceapp.feature.auth.login.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginApiDTO(
    val email: String,
    val password: String
)
