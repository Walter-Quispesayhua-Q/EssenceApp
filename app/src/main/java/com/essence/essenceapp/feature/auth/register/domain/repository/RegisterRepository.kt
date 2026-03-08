package com.essence.essenceapp.feature.auth.register.domain.repository

import com.essence.essenceapp.feature.auth.register.domain.model.Register
import com.essence.essenceapp.feature.auth.register.domain.model.RegisterRequest

interface RegisterRepository {
    suspend fun getAvailableUsername(query: String): Boolean
    suspend fun createUser(registerRequest: RegisterRequest): Register?
}