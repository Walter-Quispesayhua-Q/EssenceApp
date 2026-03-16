package com.essence.essenceapp.feature.auth.register.data.repository

import com.essence.essenceapp.feature.auth.register.data.api.RegisterApiService
import com.essence.essenceapp.feature.auth.register.data.mapper.registerRequestToDto
import com.essence.essenceapp.feature.auth.register.data.mapper.registerToDomain
import com.essence.essenceapp.feature.auth.register.domain.model.Register
import com.essence.essenceapp.feature.auth.register.domain.model.RegisterRequest
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val apiService: RegisterApiService
) : RegisterRepository {

    override suspend fun getAvailableUsername(query: String): Boolean {
        val response = apiService.getAvailableUsername(query)
        return response?.data
            ?: throw IllegalStateException("Respuesta invalida al verificar username")
    }

    override suspend fun createUser(registerRequest: RegisterRequest): Register? {
        val request = registerRequest.registerRequestToDto()
        val response = apiService.createUser(request)
        return response?.data?.registerToDomain()
    }
}
