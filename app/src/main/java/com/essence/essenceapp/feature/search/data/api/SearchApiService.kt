package com.essence.essenceapp.feature.search.data.api

import com.essence.essenceapp.feature.search.data.dto.CategoryApiDTO
import com.essence.essenceapp.feature.search.data.dto.SearchApiDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    companion object {
        const val BASE = "search"
    }

    @GET("$BASE/categories")
    suspend fun getCategories(): List<CategoryApiDTO>?

    @GET(BASE)
    suspend fun search(
        @Query("query") query: String,
        @Query("type") type: String? = null
    ): SearchApiDTO?
}