package com.essence.essenceapp.feature.profile.ui

import com.essence.essenceapp.feature.profile.domain.model.UserProfile

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
}