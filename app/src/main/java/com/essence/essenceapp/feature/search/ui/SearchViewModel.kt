package com.essence.essenceapp.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.search.domain.usecase.GetAvailableCategoriesUseCase
import com.essence.essenceapp.feature.search.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val getAvailableCategoriesUseCase: GetAvailableCategoriesUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Editing())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.QueryChanged -> updateEditing {
                it.copy(
                    form = it.form.copy(query = action.value)
                )
            }
            is SearchAction.TypeChanged -> updateEditing {
                it.copy(
                    form = it.form.copy(type = action.value)
                )
            }
            is SearchAction.Submit -> submitSearch()
            is SearchAction.ClearError -> updateEditing {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun updateEditing(transform: (SearchUiState.Editing) -> SearchUiState.Editing) {
        val current = _uiState.value
        if (current is SearchUiState.Editing) {
            _uiState.value = transform(current)
        }
    }

    private fun submitSearch() {
        val current = _uiState.value
        if (current !is SearchUiState.Editing) return
        viewModelScope.launch {
            updateEditing { it.copy(isSubmitting = true) }
            val result = searchUseCase(
                query = current.form.query,
                type = current.form.type
            )
            result.onSuccess { searchResult ->
                _uiState.value = SearchUiState.Success(
                    form = current.form,
                    results = searchResult
                )
            }
            result.onFailure {
                error -> updateEditing {
                    it.copy(
                        isSubmitting = false, errorMessage = error.message
                    )
                }
            }
        }
    }


}