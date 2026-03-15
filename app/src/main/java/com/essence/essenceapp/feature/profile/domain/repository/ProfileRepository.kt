package com.essence.essenceapp.feature.profile.domain.repository

import com.essence.essenceapp.feature.profile.domain.model.CurrentUser
import com.essence.essenceapp.feature.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getCurrentUser(): CurrentUser?
    suspend fun getUserProfile(): UserProfile?
}