package com.essence.essenceapp.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is HomeUiState.Success -> {
            val home = uiState.homeData

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "EssenceApp - Home",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Songs: ${home.songs.size}")
                Text("Albums: ${home.albums.size}")
                Text("Artists: ${home.artists.size}")
            }
        }

        is HomeUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = uiState.message)
                Button(onClick = onRefresh) {
                    Text("Reintentar")
                }
            }
        }
    }
}
