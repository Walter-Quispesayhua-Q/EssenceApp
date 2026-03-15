package com.essence.essenceapp.feature.search.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.search.ui.components.SearchContent
import com.essence.essenceapp.feature.search.ui.components.SearchTopBar

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onOpenSong: (Long) -> Unit = {},
    onOpenAlbum: (Long) -> Unit = {},
    onOpenArtist: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val query = when (val current = state) {
        is SearchUiState.Editing -> current.form.query
        is SearchUiState.Success -> current.form.query
        is SearchUiState.Idle -> ""
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = { viewModel.onAction(SearchAction.QueryChanged(it)) },
                onSearch = { viewModel.onAction(SearchAction.Submit) }
            )
        }
    ) { innerPadding ->
        SearchContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = viewModel::onAction,
            onOpenSong = onOpenSong,
            onOpenAlbum = onOpenAlbum,
            onOpenArtist = onOpenArtist
        )
    }
}