package com.essence.essenceapp.feature.profile.domain.usecase

import com.essence.essenceapp.feature.profile.domain.model.CurrentUser
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository

class GetCurrentUserUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<CurrentUser> {
        val response = repository.getCurrentUser()
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("No se pudo obtener el usuario actual"))
        }
    }
}