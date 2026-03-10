package com.essence.essenceapp.feature.search.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.search.ui.components.SearchContent
import com.essence.essenceapp.feature.search.ui.components.SearchTopBar

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onSearchSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SearchTopBar(
                query = (state as? SearchUiState.Editing)?.form?.query ?: "",
                onQueryChange = { viewModel.onAction(SearchAction.QueryChanged(it)) },
                onSearch = { viewModel.onAction(SearchAction.Submit) }
            )
        }
    ) { innerPadding ->
        SearchContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = viewModel::onAction
        )
    }
}