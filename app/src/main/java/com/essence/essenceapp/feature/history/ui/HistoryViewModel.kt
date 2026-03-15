package com.essence.essenceapp.feature.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.history.domain.usecase.GetSongsOfHistoryUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getSongsOfHistoryUseCase: GetSongsOfHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            HistoryAction.Refresh -> loadHistory()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            try {
                val result = getSongsOfHistoryUseCase()
                result.onSuccess { songs ->
                    _uiState.value = HistoryUiState.Success(songs = songs)
                }
                result.onFailure { error ->
                    _uiState.value = HistoryUiState.Error(
                        message = error.toUserMessage()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(
                    message = e.toUserMessage()
                )
            }
        }
    }
}