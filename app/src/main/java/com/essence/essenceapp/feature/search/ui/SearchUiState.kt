package com.essence.essenceapp.feature.search.ui

import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.model.Search

sealed interface SearchUiState {

    data object Idle : SearchUiState

    data class Editing(
        val form: SearchFormState = SearchFormState(),
        val categories: List<Category> = emptyList(),
        val isCategoriesLoading: Boolean = true,
        val categoriesError: String? = null,
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    ) : SearchUiState {
        val canSubmit: Boolean
            get() = form.isValid && !isSubmitting
    }

    data class Success(
        val form: SearchFormState,
        val results: Search,
        val page: Int = 0,
        val isLoadingNextPage: Boolean = false
    ) : SearchUiState {
        val hasFilter: Boolean get() = form.type.isNotBlank()
    }
}