package com.essence.essenceapp.feature.search.data.repository

import com.essence.essenceapp.feature.search.data.api.SearchApiService
import com.essence.essenceapp.feature.search.data.mapper.categoryToDomain
import com.essence.essenceapp.feature.search.data.mapper.searchToDomain
import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.model.Search
import com.essence.essenceapp.feature.search.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val apiService: SearchApiService
): SearchRepository {

    override suspend fun getCategories(): List<Category>? {
        val response = apiService.getCategories()
        return response?.mapNotNull { it.categoryToDomain() }
    }

    override suspend fun search(query: String, type: String?, page: Int): Search? {
        val response = apiService.search(query, type, page)
        return response?.searchToDomain()
    }
}
