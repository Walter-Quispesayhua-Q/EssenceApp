package com.essence.essenceapp.feature.search.ui

data class SearchFormState(
    val query: String = "",
    val type: String = ""
) {
    val trimmedQuery: String get() = query.trim()

    val isValid: Boolean
        get() = trimmedQuery.length >= MIN_QUERY_LENGTH

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}