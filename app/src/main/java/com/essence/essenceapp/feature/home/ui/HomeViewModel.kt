package com.essence.essenceapp.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.network.storage.TokenManager
import com.essence.essenceapp.feature.history.domain.usecase.GetSongsOfHistoryUseCase
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val getSongsOfHistoryUseCase: GetSongsOfHistoryUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val homeDeferred = async { homeRepository.getHome() }
                val recentSongsDeferred = async {
                    val userId = tokenManager.getUserId()
                    if (userId == null) {
                        emptyList()
                    } else {
                        try {
                            getSongsOfHistoryUseCase()
                                .getOrDefault(emptyList())
                                .take(10)
                        } catch (_: Exception) {
                            emptyList()
                        }
                    }
                }

                val data = homeDeferred.await()
                val recentSongs = recentSongsDeferred.await()

                if (data != null) {
                    _uiState.value = HomeUiState.Success(
                        homeData = data,
                        recentSongs = recentSongs
                    )
                } else {
                    _uiState.value = HomeUiState.Error(
                        message = "No se encontraron datos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.toUserMessage()
                )
            }
        }
    }

    fun onRefresh() {
        loadData()
    }
}