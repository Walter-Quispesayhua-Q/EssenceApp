package com.essence.essenceapp.feature.search.domain.usecase

import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.repository.SearchRepository

class GetAvailableCategoriesUseCase(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        val response = searchRepository.getCategories()
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(
                Exception("Error al obtener las categorias disponibles")
            )
        }
    }
}