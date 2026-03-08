package com.essence.essenceapp.feature.auth.login.data.api

import com.essence.essenceapp.feature.auth.login.data.dto.LoginApiDTO
import com.essence.essenceapp.feature.auth.login.data.dto.TokenDTO
import com.essence.essenceapp.shared.data.dto.ApiResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {

    companion object {
        const val BASE = "login"
    }

    @POST(BASE)
    suspend fun login(@Body loginApiDTO: LoginApiDTO): ApiResponseDto<TokenDTO>
}