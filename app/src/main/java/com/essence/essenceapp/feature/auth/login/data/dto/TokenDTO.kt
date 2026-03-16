package com.essence.essenceapp.feature.auth.login.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    val token: String
)
