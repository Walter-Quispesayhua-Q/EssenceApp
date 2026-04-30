package com.essence.essenceapp.feature.profile.domain.repository

import com.essence.essenceapp.feature.profile.domain.model.CurrentUser
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun refreshUserProfile(): Result<UserProfile>
    suspend fun clearCache()
    suspend fun getCurrentUser(): CurrentUser?
}