package com.essence.essenceapp.feature.register.domain.repository

import com.essence.essenceapp.feature.register.domain.model.Register
import com.essence.essenceapp.feature.register.domain.model.RegisterRequest

interface RegisterRepository {
    suspend fun getAvailableUsername(query: String): Boolean
    suspend fun createUser(registerRequest: RegisterRequest): Register?
}