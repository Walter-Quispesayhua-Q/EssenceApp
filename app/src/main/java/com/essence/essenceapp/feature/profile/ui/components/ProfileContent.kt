package com.essence.essenceapp.feature.profile.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.essence.essenceapp.feature.profile.ui.ProfileUiState
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ScreenHorizontalPadding = 20.dp
private val IslandRadius = 28.dp

private data class ProfileStatItem(
    val label: String,
    val value: Int,
    val accent: Color,
    val caption: String
)

private data class HeroMetricItem(
    val label: String,
    val value: String,
    val accent: Color
)

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState,
    onRetry: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    ProfileStateTransition(
        modifier = modifier,
        state = state,
        onRetry = onRetry,
        onBack = onBack
    )
}

@Composable
private fun ProfileStateTransition(
    modifier: Modifier,
    state: ProfileUiState,
    onRetry: () -> Unit,
    onBack: (() -> Unit)?
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        label = "profile_state_transition",
        contentKey = { it.transitionKey() },
        transitionSpec = {
            (
                fadeIn(animationSpec = tween(360, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        animationSpec = tween(360, easing = FastOutSlowInEasing),
                        initialOffsetY = { fullHeight -> fullHeight / 8 }
                    )
                ).togetherWith(
                fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                    slideOutVertically(
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        targetOffsetY = { fullHeight -> -fullHeight / 8 }
                    )
            )
        }
    ) { current ->
        when (current) {
            ProfileUiState.Loading -> ProfileLoadingSkeleton(
                modifier = Modifier.fillMaxSize()
            )

            is ProfileUiState.Error -> AppErrorState(
                modifier = Modifier.fillMaxSize(),
                title = "No se pudo cargar el perfil",
                message = current.message,
                onRetry = onRetry
            )

            is ProfileUiState.Success -> ProfileSuccessState(
                modifier = Modifier.fillMaxSize(),
                profile = current.profile,
                onBack = onBack
            )
        }
    }
}

private fun ProfileUiState.transitionKey(): Int = when (this) {
    ProfileUiState.Loading -> 0
    is ProfileUiState.Error -> 1
    is ProfileUiState.Success -> 2
}

@Composable
private fun ProfileSuccessState(
    modifier: Modifier = Modifier,
    profile: UserProfile,
    onBack: (() -> Unit)? = null
) {
    val bottomClearance = LocalBottomBarClearance.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomClearance + 24.dp)
        ) {
            item {
                ProfileHeroSection(
                    profile = profile,
                    onBack = onBack
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))

                ProfileSummaryIsland(
                    profile = profile,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))

                SectionHeader(
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                    title = "Coleccion y actividad",
                    subtitle = "Una vista rapida de tu musica guardada y tu movimiento reciente."
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                StatsMatrix(
                    profile = profile,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))

                AccountDetailsIsland(
                    profile = profile,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                )
            }
        }
    }
}

@Composable
private fun ProfileHeroSection(
    profile: UserProfile,
    onBack: (() -> Unit)? = null
) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidnightBlack)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.20f),
                            SoftRose.copy(alpha = 0.14f),
                            MidnightBlack.copy(alpha = 0.96f)
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
                            SoftRose.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 1100f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = statusBarTopPadding + 12.dp,
                    bottom = 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (onBack != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FloatingCircleButton(
                        onClick = onBack,
                        contentDescription = "Volver"
                    )
                }
            }

            ProfileToneBadge()

            ProfileAvatarOrb(name = profile.username)

            HeroInfoIsland(profile = profile)
        }
    }
}

@Composable
private fun FloatingCircleButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    Surface(
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = contentDescription,
                tint = PureWhite.copy(alpha = 0.88f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ProfileToneBadge() {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = SoftRose.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.22f))
    ) {
        Text(
            text = "Cuenta personal",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = SoftRose
        )
    }
}

@Composable
private fun ProfileAvatarOrb(
    name: String
) {
    Box(
        modifier = Modifier.size(154.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.20f),
                            MutedTeal.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier.size(132.dp),
            shape = CircleShape,
            color = GraphiteSurface.copy(alpha = 0.82f),
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f)),
            shadowElevation = 16.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftRose.copy(alpha = 0.95f),
                                    MutedTeal.copy(alpha = 0.88f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialsFrom(name),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroInfoIsland(
    profile: UserProfile
) {
    GlassIsland(
        modifier = Modifier.fillMaxWidth(),
        accent = SoftRose,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = profile.username,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.72f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Miembro desde ${formatInstant(profile.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.48f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            HeroMetricsGrid(profile = profile)
        }
    }
}

@Composable
private fun HeroMetricsGrid(
    profile: UserProfile
) {
    val metrics = listOf(
        HeroMetricItem(
            label = "Playlists",
            value = profile.totalPlaylists.toString(),
            accent = MutedTeal
        ),
        HeroMetricItem(
            label = "Historial",
            value = formatCompactCount(profile.totalPlayHistory),
            accent = LuxeGold
        ),
        HeroMetricItem(
            label = "Favoritos",
            value = formatCompactCount(totalFavorites(profile)),
            accent = SoftRose
        ),
        HeroMetricItem(
            label = if (profile.updatedAt != null) "Actualizado" else "Creado",
            value = formatShortDate(profile.updatedAt ?: profile.createdAt),
            accent = MutedTeal
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        metrics.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { item ->
                    HeroMetricCard(
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
private fun HeroMetricCard(
    modifier: Modifier = Modifier,
    item: HeroMetricItem
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = item.accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, item.accent.copy(alpha = 0.14f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelMedium,
                color = PureWhite.copy(alpha = 0.56f)
            )

            Text(
                text = item.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = item.accent
            )
        }
    }
}

@Composable
private fun ProfileSummaryIsland(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    val strongestArea = titleCase(strongestArea(profile))

    GlassIsland(
        modifier = modifier,
        accent = MutedTeal,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MutedTeal.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.18f))
            ) {
                Text(
                    text = "Panorama general",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MutedTeal
                )
            }

            Text(
                text = "Tu biblioteca mantiene buen movimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Text(
                text = buildSummaryMessage(profile),
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.64f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryHighlightPill(
                        modifier = Modifier.weight(1f),
                        label = "Favoritos",
                        value = formatCompactCount(totalFavorites(profile)),
                        accent = SoftRose
                    )

                    SummaryHighlightPill(
                        modifier = Modifier.weight(1f),
                        label = "Area fuerte",
                        value = strongestArea,
                        accent = MutedTeal
                    )
                }

                SummaryHighlightPill(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Actividad",
                    value = "${formatCompactCount(profile.totalPlayHistory)} reproducciones registradas",
                    accent = LuxeGold
                )
            }
        }
    }
}

@Composable
private fun SummaryHighlightPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accent: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.14f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = PureWhite.copy(alpha = 0.52f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
        }
    }
}

@Composable
private fun StatsMatrix(
    profile: UserProfile,
    modifier: Modifier = Modifier
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
        modifier = modifier,
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
        shape = RoundedCornerShape(26.dp),
        color = GraphiteSurface.copy(alpha = 0.62f),
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
                                item.accent.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBadge(
                        text = item.caption,
                        accent = item.accent
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(item.accent)
                    )
                }

                Text(
                    text = formatCompactCount(item.value),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )

                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PureWhite.copy(alpha = 0.68f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    item.accent.copy(alpha = 0.90f),
                                    item.accent.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun StatBadge(
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.16f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = accent
        )
    }
}

@Composable
private fun AccountDetailsIsland(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    GlassIsland(
        modifier = modifier,
        accent = LuxeGold,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = "Detalles de cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow(
                label = "Usuario",
                value = profile.username,
                accent = MutedTeal
            )

            DetailRow(
                label = "Correo",
                value = profile.email,
                accent = SoftRose
            )

            DetailRow(
                label = "Creado",
                value = formatInstant(profile.createdAt),
                accent = LuxeGold,
                showDivider = profile.updatedAt != null
            )

            profile.updatedAt?.let {
                DetailRow(
                    label = "Ultima actualizacion",
                    value = formatInstant(it),
                    accent = MutedTeal,
                    showDivider = false
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    accent: Color,
    showDivider: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = PureWhite.copy(alpha = 0.46f)
                )

                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent.copy(alpha = 0.85f))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PureWhite,
                textAlign = TextAlign.End
            )
        }

        if (showDivider) {
            HorizontalDivider(
                color = PureWhite.copy(alpha = 0.05f),
                thickness = 0.5.dp
            )
        }

        if (showDivider) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Column(
        modifier = modifier,
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
                color = PureWhite
            )
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = PureWhite.copy(alpha = 0.45f)
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
        color = GraphiteSurface.copy(alpha = 0.70f),
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

private fun totalFavorites(profile: UserProfile): Int {
    return profile.totalLikedSongs + profile.totalLikedAlbums + profile.totalLikedArtists
}

private fun strongestArea(profile: UserProfile): String {
    val values = listOf(
        "canciones" to profile.totalLikedSongs,
        "albumes" to profile.totalLikedAlbums,
        "artistas" to profile.totalLikedArtists
    )
    return values.maxByOrNull { it.second }?.first ?: "tu biblioteca"
}

private fun buildSummaryMessage(profile: UserProfile): String {
    return "Tu cuenta reune ${formatCompactCount(totalFavorites(profile))} favoritos, ${formatCompactCount(profile.totalPlaylists)} playlists y ${formatCompactCount(profile.totalPlayHistory)} reproducciones registradas. Hoy el foco mas fuerte esta en ${strongestArea(profile)}."
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

private fun titleCase(value: String): String {
    return value
        .split(" ")
        .joinToString(" ") { part ->
            if (part.isBlank()) part
            else part.take(1).uppercase() + part.drop(1)
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

private fun formatCompactCount(value: Int): String {
    return when {
        value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
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
            ProfileContent(
                state = ProfileUiState.Loading,
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Profile - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ErrorPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(
                state = ProfileUiState.Error("No se pudo cargar el perfil."),
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Profile - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            ProfileContent(
                state = ProfileUiState.Success(previewProfile),
                onRetry = {},
                onBack = {}
            )
        }
    }
}