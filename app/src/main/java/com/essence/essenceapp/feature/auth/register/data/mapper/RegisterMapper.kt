package com.essence.essenceapp.feature.auth.register.data.mapper

import com.essence.essenceapp.feature.auth.register.data.dto.RegisterApiDTO
import com.essence.essenceapp.feature.auth.register.domain.model.Register

fun RegisterApiDTO.registerToDomain(): Register? {
    return Register(
        id = this.id ?: return null,
        username = this.username ?: return null,
        email = this.email ?: return null
    )
}