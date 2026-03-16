package com.essence.essenceapp.feature.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.network.storage.TokenManager
import com.essence.essenceapp.feature.history.domain.usecase.GetSongsOfHistoryUseCase
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
            Log.e("HOME_DEBUG", "loadData START")
            _uiState.value = HomeUiState.Loading

            val recentSongs = emptyList<com.essence.essenceapp.feature.song.domain.model.SongSimple>()
            Log.e("HOME_DEBUG", "history SKIPPED")

            val homeResult = runCatching {
                Log.e("HOME_DEBUG", "before homeRepository.getHome()")
                homeRepository.getHome()
            }

            homeResult
                .onSuccess { data ->
                    Log.e(
                        "HOME_DEBUG",
                        "home success dataNull=${data == null} songs=${data?.songs?.size} albums=${data?.albums?.size} artists=${data?.artists?.size}"
                    )

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
                }
                .onFailure { error ->
                    Log.e("HOME_DEBUG", "home failure=${error.message}", error)
                    _uiState.value = HomeUiState.Error(
                        message = error.toUserMessage()
                    )
                }
        }
    }

    fun onRefresh() {
        loadData()
    }
}