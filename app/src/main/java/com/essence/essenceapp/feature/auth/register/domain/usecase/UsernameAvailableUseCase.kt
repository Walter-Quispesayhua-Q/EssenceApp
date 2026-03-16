package com.essence.essenceapp.feature.auth.register.domain.usecase

import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterRepository
import retrofit2.HttpException

class UsernameAvailableUseCase(
    private val registerRepository: RegisterRepository
) {
    suspend operator fun invoke(query: String): Result<Boolean> {
        if (query.isBlank() || query.length < 3) {
            return Result.failure(Exception("El usuario tiene que tener más de 3 caracteres"))
        }

        return try {
            val response = registerRepository.getAvailableUsername(query)
            Result.success(response)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.failure(Exception("No autorizado para verificar el usuario"))
                400 -> Result.failure(Exception("Solicitud invalida al verificar el usuario"))
                else -> Result.failure(Exception("Error al verificar el usuario: HTTP ${e.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}