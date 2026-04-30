package com.essence.essenceapp.feature.home.data.repository

import com.essence.essenceapp.core.network.auth.SessionManager
import com.essence.essenceapp.feature.home.data.api.HomeApiService
import com.essence.essenceapp.feature.home.data.mapper.homeToDomain
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import com.essence.essenceapp.shared.cache.QueueCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val apiService: HomeApiService,
    private val queueCache: QueueCache,
    sessionManager: SessionManager
) : HomeRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val homeMemoryCache = MutableStateFlow<Home?>(null)

    init {
        scope.launch {
            sessionManager.sessionExpiredEvents.collect {
                clearMemory()
            }
        }
    }

    override fun observeHome(): StateFlow<Home?> = homeMemoryCache.asStateFlow()

    override suspend fun refreshHome(): Result<Home?> = runCatching {
        val mapped = apiService.getHome()?.homeToDomain()
        mapped?.songs?.let { queueCache.set("home", it) }
        homeMemoryCache.value = mapped
        mapped
    }

    override fun clearMemory() {
        homeMemoryCache.value = null
    }
}
