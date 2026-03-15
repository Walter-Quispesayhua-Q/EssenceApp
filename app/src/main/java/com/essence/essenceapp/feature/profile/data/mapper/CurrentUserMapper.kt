package com.essence.essenceapp.feature.profile.data.mapper

import com.essence.essenceapp.feature.profile.data.dto.CurrentUserApiDTO
import com.essence.essenceapp.feature.profile.domain.model.CurrentUser

fun CurrentUserApiDTO.currentUserToDomain(): CurrentUser? {
    return CurrentUser(
        id = this.id ?: return null,
        username = this.username ?: return null,
        email = this.email ?: return null,
        createdAt = this.createdAt ?: return null,
        updatedAt = this.updatedAt
    )
}