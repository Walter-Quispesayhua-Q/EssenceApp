package com.essence.essenceapp.feature.auth.register.domain.usecase

import com.essence.essenceapp.feature.auth.register.domain.model.Register
import com.essence.essenceapp.feature.auth.register.domain.model.RegisterRequest
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterRepository

class RegisterUseCase(
    private val registerRepository: RegisterRepository
) {
    suspend operator fun invoke(registerRequest: RegisterRequest): Result<Register> {
        if (registerRequest.username.isBlank()) {
            return Result.failure(Exception("El usuario es requerido"))
        }
        if (registerRequest.email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        if (registerRequest.password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }

        return try {
            val response = registerRepository.createUser(registerRequest)
            if (response != null) {
                Result.success(response)
            } else {
                Result.failure(
                    Exception("No se pudo completar el registro.")
                )
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
