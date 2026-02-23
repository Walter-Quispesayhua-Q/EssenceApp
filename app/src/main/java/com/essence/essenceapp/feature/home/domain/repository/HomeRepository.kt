package com.essence.essenceapp.feature.home.domain.repository

import com.essence.essenceapp.feature.home.domain.model.Home

interface HomeRepository {
    suspend fun getHome(): Home?
}