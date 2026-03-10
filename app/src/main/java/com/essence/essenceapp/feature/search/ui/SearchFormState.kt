package com.essence.essenceapp.feature.search.ui

data class SearchFormState(
    val query: String = "",
    val type: String = ""
) {
    val isValid: Boolean
        get() = query.isNotBlank()
}