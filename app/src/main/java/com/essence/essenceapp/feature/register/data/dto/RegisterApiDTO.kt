package com.essence.essenceapp.feature.register.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterApiDTO(
    val id: Long?,
    val username: String?,
    val email: String?
)