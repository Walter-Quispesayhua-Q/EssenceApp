package com.essence.essenceapp.feature.profile.data.api

import com.essence.essenceapp.feature.profile.data.dto.CurrentUserApiDTO
import com.essence.essenceapp.feature.profile.data.dto.UserDetailApiDTO
import com.essence.essenceapp.shared.data.dto.ApiResponseDto
import retrofit2.http.GET

interface ProfileApiService {

    companion object {
        const val BASE = "user"
    }

    @GET("$BASE/me")
    suspend fun getCurrentUser(): ApiResponseDto<CurrentUserApiDTO>?

    @GET("$BASE/profile")
    suspend fun getUserProfile(): ApiResponseDto<UserDetailApiDTO>?
}