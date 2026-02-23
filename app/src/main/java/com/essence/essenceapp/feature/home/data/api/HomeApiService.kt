package com.essence.essenceapp.feature.home.data.api

import com.essence.essenceapp.feature.home.data.dto.HomeResponseApiDTO
import retrofit2.http.GET

interface HomeApiService {
    @GET("home")
    suspend fun getHome(): HomeResponseApiDTO?
}