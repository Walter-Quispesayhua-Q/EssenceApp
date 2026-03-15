package com.essence.essenceapp.feature.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.ui.ProfileUiState
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val HorizontalPadding = 16.dp
private val VerticalGap = 12.dp

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState,
    onRetry: () -> Unit
) {
    when (state) {
        ProfileUiState.Loading -> ProfileLoadingState(modifier = modifier)

        is ProfileUiState.Error -> AppErrorState(
            modifier = modifier,
            title = "No se pudo cargar el perfil",
            message = state.message,
            onRetry = onRetry
        )

        is ProfileUiState.Success -> ProfileSuccessState(
            modifier = modifier,
            profile = state.profile
        )
    }
}

@Composable
private fun ProfileLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProfileSuccessState(
    modifier: Modifier = Modifier,
    profile: UserProfile
) {
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = HorizontalPadding,
            end = HorizontalPadding,
            top = 12.dp,
            bottom = bottomClearance + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(VerticalGap)
    ) {
        item {
            ProfileHeaderCard(profile = profile)
        }

        item {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            StatsGrid(profile = profile)
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    profile: UserProfile
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = profile.username,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            Text(
                text = "Miembro desde ${formatInstant(profile.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            profile.updatedAt?.let {
                Text(
                    text = "Actualizado ${formatInstant(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(
    profile: UserProfile
) {
    val stats = listOf(
        "Canciones con like" to profile.totalLikedSongs.toString(),
        "Álbumes con like" to profile.totalLikedAlbums.toString(),
        "Artistas con like" to profile.totalLikedArtists.toString(),
        "Playlists" to profile.totalPlaylists.toString(),
        "Historial" to profile.totalPlayHistory.toString()
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(stats) { stat ->
            StatCard(
                label = stat.first,
                value = stat.second
            )
        }

        item(span = { GridItemSpan(2) }) {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                androidx.compose.foundation.layout.Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Actividad",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tu biblioteca y tu actividad se irán reflejando aquí conforme sigas usando la app.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatInstant(value: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
    return value.atZone(ZoneId.systemDefault()).format(formatter)
}

private val previewProfile = UserProfile(
    id = 1L,
    username = "Walter",
    email = "walter@email.com",
    createdAt = Instant.parse("2025-01-01T10:15:30Z"),
    updatedAt = Instant.parse("2026-03-10T18:20:00Z"),
    totalLikedSongs = 24,
    totalLikedAlbums = 8,
    totalLikedArtists = 6,
    totalPlaylists = 5,
    totalPlayHistory = 142
)

@Preview(name = "Profile - Loading", showBackground = true)
@Composable
private fun ProfileContentLoadingPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(
                state = ProfileUiState.Loading,
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Profile - Error", showBackground = true)
@Composable
private fun ProfileContentErrorPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(
                state = ProfileUiState.Error("No se pudo cargar el perfil."),
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Profile - Success", showBackground = true)
@Composable
private fun ProfileContentSuccessPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(
                state = ProfileUiState.Success(previewProfile),
                onRetry = {}
            )
        }
    }
}