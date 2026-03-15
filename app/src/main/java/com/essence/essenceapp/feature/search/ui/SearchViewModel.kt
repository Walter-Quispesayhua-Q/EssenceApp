package com.essence.essenceapp.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.usecase.GetAvailableCategoriesUseCase
import com.essence.essenceapp.feature.search.domain.usecase.SearchUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val getAvailableCategoriesUseCase: GetAvailableCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Editing())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var cachedCategories: List<Category> = emptyList()

    init {
        loadCategories()
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.QueryChanged -> updateEditing {
                it.copy(form = it.form.copy(query = action.value))
            }

            is SearchAction.TypeChanged -> updateEditing {
                it.copy(form = it.form.copy(type = action.value))
            }

            SearchAction.Submit -> submitSearch()

            SearchAction.ClearError -> updateEditing {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            updateEditing {
                it.copy(
                    isCategoriesLoading = true,
                    categoriesError = null
                )
            }

            try {
                val result = getAvailableCategoriesUseCase()
                result.onSuccess { categories ->
                    cachedCategories = categories
                    updateEditing {
                        it.copy(
                            categories = categories,
                            isCategoriesLoading = false,
                            categoriesError = null
                        )
                    }
                }
                result.onFailure { error ->
                    updateEditing {
                        it.copy(
                            isCategoriesLoading = false,
                            categoriesError = error.toUserMessage()
                        )
                    }
                }
            } catch (e: Exception) {
                updateEditing {
                    it.copy(
                        isCategoriesLoading = false,
                        categoriesError = e.toUserMessage()
                    )
                }
            }
        }
    }

    private fun submitSearch() {
        val form = currentForm()
        if (form.query.isBlank()) {
            updateEditing {
                it.copy(errorMessage = "Escribe algo para buscar")
            }
            return
        }

        viewModelScope.launch {
            updateEditing {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            }

            try {
                val result = searchUseCase(
                    query = form.query,
                    type = form.type.takeIf { it.isNotBlank() }
                )

                result.onSuccess { searchResult ->
                    _uiState.value = SearchUiState.Success(
                        form = form,
                        results = searchResult
                    )
                }

                result.onFailure { error ->
                    updateEditing {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.toUserMessage()
                        )
                    }
                }
            } catch (e: Exception) {
                updateEditing {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.toUserMessage()
                    )
                }
            }
        }
    }

    private fun currentForm(): SearchFormState {
        return when (val current = _uiState.value) {
            is SearchUiState.Editing -> current.form
            is SearchUiState.Success -> current.form
            is SearchUiState.Idle -> SearchFormState()
        }
    }

    private fun updateEditing(transform: (SearchUiState.Editing) -> SearchUiState.Editing) {
        val editing = when (val current = _uiState.value) {
            is SearchUiState.Editing -> current
            is SearchUiState.Success -> SearchUiState.Editing(
                form = current.form,
                categories = cachedCategories,
                isCategoriesLoading = false
            )
            is SearchUiState.Idle -> SearchUiState.Editing(
                categories = cachedCategories,
                isCategoriesLoading = false
            )
        }
        _uiState.value = transform(editing)
    }
}
