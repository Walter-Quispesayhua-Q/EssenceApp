package com.essence.essenceapp.feature.search.ui

sealed interface SearchAction {
    data class QueryChanged(val value: String): SearchAction
    data class TypeChanged(val value: String): SearchAction
    data object Submit : SearchAction
    data object ClearError: SearchAction
}