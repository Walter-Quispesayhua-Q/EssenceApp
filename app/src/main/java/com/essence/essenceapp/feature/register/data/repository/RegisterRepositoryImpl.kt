package com.essence.essenceapp.feature.register.data.repository

import com.essence.essenceapp.feature.register.data.api.RegisterApiService
import com.essence.essenceapp.feature.register.data.mapper.registerRequestToDto
import com.essence.essenceapp.feature.register.data.mapper.registerToDomain
import com.essence.essenceapp.feature.register.domain.model.Register
import com.essence.essenceapp.feature.register.domain.model.RegisterRequest
import com.essence.essenceapp.feature.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val apiService: RegisterApiService
): RegisterRepository {

    override suspend fun getAvailableUsername(query: String): Boolean {
        return apiService.getAvailableUsername(query)
    }

    override suspend fun createUser(registerRequest: RegisterRequest): Register? {
        val request = registerRequest.registerRequestToDto()
        val response = apiService.createUser(request)
        return response?.data?.registerToDomain()
    }


}