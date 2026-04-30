package com.essence.essenceapp.feature.profile.data.repository

import com.essence.essenceapp.core.network.auth.SessionManager
import com.essence.essenceapp.feature.profile.data.api.ProfileApiService
import com.essence.essenceapp.feature.profile.data.local.ProfileLocalDataSource
import com.essence.essenceapp.feature.profile.data.mapper.currentUserToDomain
import com.essence.essenceapp.feature.profile.data.mapper.userProfileToDomain
import com.essence.essenceapp.feature.profile.domain.model.CurrentUser
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService,
    private val localDataSource: ProfileLocalDataSource,
    sessionManager: SessionManager
) : ProfileRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            sessionManager.sessionExpiredEvents.collect {
                localDataSource.clear()
            }
        }
    }

    override fun observeUserProfile(): Flow<UserProfile?> {
        return localDataSource.cachedProfile
    }

    override suspend fun refreshUserProfile(): Result<UserProfile> {
        return runCatching {
            val remote = apiService.getUserProfile()?.data?.userProfileToDomain()
                ?: throw IllegalStateException("Respuesta vacia del servidor")
            localDataSource.save(remote)
            remote
        }
    }

    override suspend fun clearCache() {
        localDataSource.clear()
    }

    override suspend fun getCurrentUser(): CurrentUser? {
        return apiService.getCurrentUser()?.data?.currentUserToDomain()
    }
}