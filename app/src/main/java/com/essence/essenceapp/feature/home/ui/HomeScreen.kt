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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(viewModel: HomeViewModel){

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "EssenceApp - Home",
                    style =
                        MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Songs: ${currentState.homeData.songs.size}")
                Text("Albums: ${currentState.homeData.albums.size}")
                Text("Artists: ${currentState.homeData.artists.size}")
            }
        }
        is HomeUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = currentState.message)
                Button(onClick = {viewModel.onRefresh()}) {
                    Text("Reintentar")
                }
            }
        }
    }
}