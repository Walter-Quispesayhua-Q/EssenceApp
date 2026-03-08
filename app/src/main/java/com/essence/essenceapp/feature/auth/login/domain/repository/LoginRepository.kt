package com.essence.essenceapp.feature.auth.login.domain.repository

import com.essence.essenceapp.feature.auth.login.domain.model.Login
import com.essence.essenceapp.feature.auth.login.domain.model.Token

interface LoginRepository {
    suspend fun login(login: Login): Token?
}