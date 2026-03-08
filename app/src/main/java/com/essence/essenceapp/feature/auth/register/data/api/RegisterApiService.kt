package com.essence.essenceapp.feature.auth.register.data.api

import com.essence.essenceapp.feature.auth.register.data.dto.RegisterApiDTO
import com.essence.essenceapp.feature.auth.register.data.dto.RegisterRequestApiDTO
import com.essence.essenceapp.shared.data.dto.ApiResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RegisterApiService {
    companion object {
        const val BASE = "register"
    }

    @GET(BASE)
    suspend fun getAvailableUsername(@Query("query") query: String): Boolean

    @POST(BASE)
    suspend fun createUser(@Body registerRequestApiDTO: RegisterRequestApiDTO): ApiResponseDto<RegisterApiDTO>?
}