package com.essence.essenceapp.feature.search.domain.usecase

import com.essence.essenceapp.feature.search.domain.model.Search
import com.essence.essenceapp.feature.search.domain.repository.SearchRepository

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String, type: String? = null): Result<Search> {
        if (query.isBlank()) {
            return Result.failure(Exception("Escribe algo para buscar"))
        }
        val response = searchRepository.search(query, type)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(
                Exception("Error en la busqueda")
            )
        }
    }
}