package com.essence.essenceapp.feature.auth.login.domain.usecase

import com.essence.essenceapp.feature.auth.login.domain.model.Login
import com.essence.essenceapp.feature.auth.login.domain.model.Token
import com.essence.essenceapp.feature.auth.login.domain.repository.LoginRepository

class LoginUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(login: Login): Result<Token> {

        if (login.email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }

        if (login.password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }

       val token = loginRepository.login(login)
        return if (token != null) {
            Result.success(token)
        } else {
            Result.failure(
                Exception("Error al iniciar sesión")
            )
        }
    }
}