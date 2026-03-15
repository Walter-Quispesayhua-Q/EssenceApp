package com.essence.essenceapp.feature.profile.domain.usecase

import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository

class GetUserProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        val response = repository.getUserProfile()
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("No se pudo obtener el perfil"))
        }
    }
}