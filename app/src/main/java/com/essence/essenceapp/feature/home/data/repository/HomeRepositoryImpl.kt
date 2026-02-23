package com.essence.essenceapp.feature.home.data.repository

import com.essence.essenceapp.feature.home.data.api.HomeApiService
import com.essence.essenceapp.feature.home.data.mapper.homeToDomain
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val apiService: HomeApiService
): HomeRepository {

    override suspend fun getHome(): Home? {
        val apiDTO = apiService.getHome()
        return apiDTO?.homeToDomain()
    }
}