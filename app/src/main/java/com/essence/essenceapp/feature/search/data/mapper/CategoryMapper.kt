package com.essence.essenceapp.feature.search.data.mapper

import com.essence.essenceapp.feature.search.data.dto.CategoryApiDTO
import com.essence.essenceapp.feature.search.domain.model.Category

fun CategoryApiDTO.categoryToDomain(): Category? {
    return Category(
        value = this.value ?: return null,
        label = this.label ?: return null
    )
}