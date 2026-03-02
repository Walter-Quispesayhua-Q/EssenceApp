package com.essence.essenceapp.shared.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val message: String? = null,
    val data: T? = null
)
