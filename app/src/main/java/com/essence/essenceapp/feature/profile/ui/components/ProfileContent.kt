package com.essence.essenceapp.feature.profile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.ui.ProfileUiState
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ScreenHorizontalPadding = 20.dp
private val SectionGap = 18.dp
private val IslandRadius = 28.dp

private data class ProfileStatItem(
    val label: String,
    val value: Int,
    val accent: Color,
    val caption: String
)

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
        GlassIsland(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding),
            accent = SoftRose
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    color = SoftRose,
                    strokeWidth = 2.5.dp
                )
                Text(
                    text = "Cargando perfil...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
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
            start = ScreenHorizontalPadding,
            end = ScreenHorizontalPadding,
            top = 14.dp,
            bottom = bottomClearance + 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        item {
            HeroProfileIsland(profile = profile)
        }

        item {
            ProfileSummaryIsland(profile = profile)
        }

        item {
            SectionHeader(
                title = "Tu biblioteca",
                subtitle = "Favoritos, playlists y actividad"
            )
        }

        item {
            StatsMatrix(profile = profile)
        }

        item {
            AccountDetailsIsland(profile = profile)
        }
    }
}

@Composable
private fun HeroProfileIsland(
    profile: UserProfile
) {
    GlassIsland(
        accent = SoftRose,
        contentPadding = PaddingValues(22.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Cuenta personal",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = SoftRose
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileMonogram(name = profile.username)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = profile.username,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )

                    Text(
                        text = "Miembro desde ${formatInstant(profile.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricPill(
                    label = "Playlists",
                    value = profile.totalPlaylists.toString(),
                    accent = MutedTeal
                )

                MetricPill(
                    label = "Historial",
                    value = profile.totalPlayHistory.toString(),
                    accent = LuxeGold
                )

                profile.updatedAt?.let {
                    MetricPill(
                        label = "Actualizado",
                        value = formatShortDate(it),
                        accent = SoftRose
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMonogram(
    name: String
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SoftRose.copy(alpha = 0.92f),
                        MutedTeal.copy(alpha = 0.88f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsFrom(name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
    }
}

@Composable
private fun ProfileSummaryIsland(
    profile: UserProfile
) {
    val totalFavorites = profile.totalLikedSongs + profile.totalLikedAlbums + profile.totalLikedArtists
    val strongestArea = strongestArea(profile)

    GlassIsland(
        accent = MutedTeal,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Tienes $totalFavorites favoritos entre canciones, albumes y artistas. Tu senal mas fuerte hoy esta en $strongestArea.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun StatsMatrix(
    profile: UserProfile
) {
    val stats = listOf(
        ProfileStatItem(
            label = "Canciones con like",
            value = profile.totalLikedSongs,
            accent = SoftRose,
            caption = "Guardadas"
        ),
        ProfileStatItem(
            label = "Albumes con like",
            value = profile.totalLikedAlbums,
            accent = LuxeGold,
            caption = "Coleccionados"
        ),
        ProfileStatItem(
            label = "Artistas con like",
            value = profile.totalLikedArtists,
            accent = MutedTeal,
            caption = "Seguidos"
        ),
        ProfileStatItem(
            label = "Playlists",
            value = profile.totalPlaylists,
            accent = SoftRose,
            caption = "Creadas"
        ),
        ProfileStatItem(
            label = "Historial",
            value = profile.totalPlayHistory,
            accent = LuxeGold,
            caption = "Reproducciones"
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { item ->
                    StatMetricCard(
                        modifier = Modifier.weight(1f),
                        item = item
                    )
                }

                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    modifier: Modifier = Modifier,
    item: ProfileStatItem
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(item.accent)
            )

            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Text(
                text = item.value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = item.caption,
                style = MaterialTheme.typography.bodySmall,
                color = item.accent.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun AccountDetailsIsland(
    profile: UserProfile
) {
    GlassIsland(
        accent = LuxeGold,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Detalles de cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            DetailRow(label = "Correo", value = profile.email)
            DetailRow(label = "Creado", value = formatInstant(profile.createdAt))

            profile.updatedAt?.let {
                DetailRow(label = "Ultima actualizacion", value = formatInstant(it))
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(LuxeGold, LuxeGold.copy(alpha = 0.3f))
                        )
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
        )
    }
}

@Composable
private fun GlassIsland(
    modifier: Modifier = Modifier,
    accent: Color,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(IslandRadius),
        color = GraphiteSurface.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.05f))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

private fun strongestArea(profile: UserProfile): String {
    val values = listOf(
        "canciones" to profile.totalLikedSongs,
        "albumes" to profile.totalLikedAlbums,
        "artistas" to profile.totalLikedArtists
    )
    return values.maxByOrNull { it.second }?.first ?: "tu biblioteca"
}

private fun initialsFrom(name: String): String {
    val pieces = name
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        pieces.isEmpty() -> "?"
        pieces.size == 1 -> pieces.first().take(1).uppercase()
        else -> (pieces.first().take(1) + pieces.last().take(1)).uppercase()
    }
}

private fun formatInstant(value: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
    return value.atZone(ZoneId.systemDefault()).format(formatter)
}

private fun formatShortDate(value: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "ES"))
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

@Preview(name = "Profile - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(state = ProfileUiState.Loading, onRetry = {})
        }
    }
}

@Preview(name = "Profile - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ErrorPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(state = ProfileUiState.Error("No se pudo cargar el perfil."), onRetry = {})
        }
    }
}

@Preview(name = "Profile - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(state = ProfileUiState.Success(previewProfile), onRetry = {})
        }
    }
}