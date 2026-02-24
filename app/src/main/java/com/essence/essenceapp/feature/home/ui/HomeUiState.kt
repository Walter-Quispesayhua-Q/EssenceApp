package com.essence.essenceapp.feature.home.ui

import com.essence.essenceapp.feature.home.domain.model.Home

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Success(
        val homeData: Home
    ): HomeUiState

    data class Error(
        val message: String
    ): HomeUiState
}