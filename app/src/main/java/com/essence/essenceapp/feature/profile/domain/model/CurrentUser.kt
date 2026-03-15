package com.essence.essenceapp.feature.profile.domain.model

import java.time.Instant

data class CurrentUser(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: Instant,
    val updatedAt: Instant?
)