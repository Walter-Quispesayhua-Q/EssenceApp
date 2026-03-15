package com.essence.essenceapp.feature.profile.data.repository

import com.essence.essenceapp.feature.profile.data.api.ProfileApiService
import com.essence.essenceapp.feature.profile.data.mapper.currentUserToDomain
import com.essence.essenceapp.feature.profile.data.mapper.userProfileToDomain
import com.essence.essenceapp.feature.profile.domain.model.CurrentUser
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val apiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getCurrentUser(): CurrentUser? {
        return apiService.getCurrentUser()?.data?.currentUserToDomain()
    }

    override suspend fun getUserProfile(): UserProfile? {
        return apiService.getUserProfile()?.data?.userProfileToDomain()
    }
}