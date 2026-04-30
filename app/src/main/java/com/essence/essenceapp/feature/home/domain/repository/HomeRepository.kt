package com.essence.essenceapp.feature.home.domain.repository

import com.essence.essenceapp.feature.home.domain.model.Home
import kotlinx.coroutines.flow.StateFlow

interface HomeRepository {
    fun observeHome(): StateFlow<Home?>
    suspend fun refreshHome(): Result<Home?>
    fun clearMemory()
}