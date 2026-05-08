package com.essence.essenceapp.feature.search.domain.usecase

import com.essence.essenceapp.feature.search.domain.model.Search
import com.essence.essenceapp.feature.search.domain.repository.SearchRepository

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(
        query: String,
        type: String? = null,
        page: Int = 0
    ): Result<Search> {
        if (query.isBlank()) return Result.failure(Exception("Escribe algo para buscar"))
        return runCatching {
            searchRepository.search(query, type, page)
                ?: throw Exception("Error en la busqueda")
        }
    }
}