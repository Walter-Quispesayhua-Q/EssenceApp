package com.essence.essenceapp.feature.home.data.repository

import android.util.Log
import com.essence.essenceapp.feature.home.data.api.HomeApiService
import com.essence.essenceapp.feature.home.data.mapper.homeToDomain
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val apiService: HomeApiService
): HomeRepository {

    override suspend fun getHome(): Home? {
        val apiDTO = apiService.getHome()
        Log.e(
            "HOME_DEBUG",
            "api songs=${apiDTO?.songs?.size} albums=${apiDTO?.albums?.size} artists=${apiDTO?.artists?.size} status=${apiDTO?.status}"
        )

        val mapped = apiDTO?.homeToDomain()

        Log.e(
            "HOME_DEBUG",
            "mapped songs=${mapped?.songs?.size} albums=${mapped?.albums?.size} artists=${mapped?.artists?.size}"
        )

        return mapped
    }
}