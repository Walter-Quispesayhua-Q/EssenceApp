package com.essence.essenceapp.feature.history.ui

sealed interface HistoryAction {
    data object Refresh : HistoryAction
}
