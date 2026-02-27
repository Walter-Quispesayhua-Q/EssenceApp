package com.essence.essenceapp.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val data = homeRepository.getHome()
                if (data != null) {
                    _uiState.value = HomeUiState.Success(homeData = data)
                } else {
                    _uiState.value = HomeUiState.Error(message = "No se encontraron datos")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(message = e.message ?: "Error del sistema")
            }
        }
    }

    fun onRefresh() {
        loadData()
    }
}