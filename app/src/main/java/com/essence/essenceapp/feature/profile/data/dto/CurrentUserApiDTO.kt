package com.essence.essenceapp.feature.profile.data.dto

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CurrentUserApiDTO(
    val id: Long?,
    val username: String?,
    val email: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?
)