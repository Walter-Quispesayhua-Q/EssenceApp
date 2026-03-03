package com.essence.essenceapp.feature.register.data.mapper

import com.essence.essenceapp.feature.register.data.dto.RegisterRequestApiDTO
import com.essence.essenceapp.feature.register.domain.model.RegisterRequest

fun RegisterRequest.registerRequestToDto(): RegisterRequestApiDTO {
    return RegisterRequestApiDTO(
        username = this.username,
        email = this.email,
        password = this.password
    )
}