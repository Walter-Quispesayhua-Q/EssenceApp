package com.essence.essenceapp.feature.auth.login.data.repository

import com.essence.essenceapp.feature.auth.login.data.api.LoginApiService
import com.essence.essenceapp.feature.auth.login.data.mapper.loginToDto
import com.essence.essenceapp.feature.auth.login.data.mapper.tokenToDomain
import com.essence.essenceapp.feature.auth.login.domain.model.Login
import com.essence.essenceapp.feature.auth.login.domain.model.Token
import com.essence.essenceapp.feature.auth.login.domain.repository.LoginRepository

class LoginRepositoryImpl(private val apiService: LoginApiService): LoginRepository {

    override suspend fun login(login: Login): Token? {
        return try {
            val request = login.loginToDto()
            val response = apiService.login(request)
            response.data?.tokenToDomain()
        } catch (e: Exception) {
            null
        }
    }
}