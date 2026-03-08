package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.ui.HomeUiState
import com.essence.essenceapp.feature.home.ui.preview.SampleData
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onRefresh: () -> Unit
) {
    when (state) {
        is HomeUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            Column(modifier = modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Canciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Álbumes",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Artistas",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        is HomeUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadedPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Success(
                homeData = SampleData.home
            ),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Loading,
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Error(message = "Error al cargar datos"),
            onRefresh = {}
        )
    }
}