package com.essence.essenceapp.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.profile.domain.usecase.ObserveUserProfileUseCase
import com.essence.essenceapp.feature.profile.domain.usecase.RefreshUserProfileUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val refreshUserProfileUseCase: RefreshUserProfileUseCase
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        observeUserProfileUseCase(),
        refreshing,
        refreshError
    ) { profile, isRefreshing, error ->
        when {
            profile != null -> ProfileUiState.Success(
                profile = profile,
                isRefreshing = isRefreshing
            )
            isRefreshing -> ProfileUiState.Loading
            error != null -> ProfileUiState.Error(error)
            else -> ProfileUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileUiState.Loading
    )

    init {
        refresh()
    }

    fun onRefresh() {
        refresh()
    }

    fun silentRefresh() {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            refreshError.value = null

            refreshUserProfileUseCase()
                .onFailure { error ->
                    refreshError.value = error.toUserMessage()
                }

            refreshing.value = false
        }
    }
}