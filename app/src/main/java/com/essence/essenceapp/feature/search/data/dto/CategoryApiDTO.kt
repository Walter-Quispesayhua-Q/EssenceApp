package com.essence.essenceapp.feature.search.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryApiDTO(
    val value: String?,
    val label: String?
)