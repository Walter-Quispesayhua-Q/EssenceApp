package com.essence.essenceapp.feature.profile.data.dto

import java.time.Instant

data class CurrentUserApiDTO(
    val id: Long?,
    val username: String?,
    val email: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?
)