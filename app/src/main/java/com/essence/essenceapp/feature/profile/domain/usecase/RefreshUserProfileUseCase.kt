package com.essence.essenceapp.feature.profile.domain.usecase

import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class RefreshUserProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> = repository.refreshUserProfile()
}
