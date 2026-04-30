package com.essence.essenceapp.feature.search.ui

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.search.ui.components.SearchContent
import com.essence.essenceapp.feature.search.ui.components.SearchTopBar
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onOpenSong: (PlaybackOpenRequest) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {},
    onOpenArtist: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val query = when (val current = state) {
        is SearchUiState.Editing -> current.form.query
        is SearchUiState.Success -> current.form.query
        is SearchUiState.Idle -> ""
    }

    val activeTypeLabel = when (val current = state) {
        is SearchUiState.Editing -> resolveTypeLabel(current.form.type, current.categories)
        is SearchUiState.Success -> resolveTypeLabel(
            current.form.type,
            (state as? SearchUiState.Editing)?.categories.orEmpty()
        )
        is SearchUiState.Idle -> null
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = { viewModel.onAction(SearchAction.QueryChanged(it)) },
                onSearch = { viewModel.onAction(SearchAction.Submit) },
                activeTypeLabel = activeTypeLabel
            )
        }
    ) { innerPadding ->
        SearchContent(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding(),
            state = state,
            onAction = viewModel::onAction,
            onOpenSong = onOpenSong,
            onOpenAlbum = onOpenAlbum,
            onOpenArtist = onOpenArtist
        )
    }
}

private fun resolveTypeLabel(
    type: String,
    categories: List<com.essence.essenceapp.feature.search.domain.model.Category>
): String? {
    if (type.isBlank()) return null
    return categories.firstOrNull { it.value == type }?.label
        ?: type.replaceFirstChar { it.uppercase() }
}