package com.essence.essenceapp.feature.auth.register.domain.usecase

import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterRepository

class UsernameAvailableUseCase(
    private val registerRepository: RegisterRepository
) {
    suspend operator fun invoke(query: String): Result<Boolean> {
        if (query.isBlank() || query.length < 3) {
            return Result.failure(Exception("El usuario tiene que tener más de 3 caracteres"))
        }
        val response = registerRepository.getAvailableUsername(query)
        return Result.success(response)
    }
}