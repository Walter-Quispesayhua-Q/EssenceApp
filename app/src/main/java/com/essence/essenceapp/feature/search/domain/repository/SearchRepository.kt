package com.essence.essenceapp.feature.search.domain.repository

import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.model.Search

interface SearchRepository {
    suspend fun getCategories(): List<Category>?
    suspend fun search(query: String, type: String? = null): Search?
}