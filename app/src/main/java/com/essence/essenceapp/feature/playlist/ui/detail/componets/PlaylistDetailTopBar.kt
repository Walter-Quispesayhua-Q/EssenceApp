package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailTopBar(
    title: String,
    collapsedFraction: Float = 1f,
    onBack: () -> Unit = {},
    onMore: () -> Unit = {}
) {
    val fraction = collapsedFraction.coerceIn(0f, 1f)
    val containerColor = lerp(
        start = Color.Transparent,
        stop = MidnightBlack.copy(alpha = 0.95f),
        fraction = fraction
    )

    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = containerColor,
            tonalElevation = (4f * fraction).dp
        ) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        if (fraction > 0.5f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MutedTeal.copy(alpha = 0.2f * fraction),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Preview(name = "Detail TopBar - Expanded", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistDetailTopBarExpandedPreview() {
    EssenceAppTheme {
        PlaylistDetailTopBar(title = "Mi Playlist", collapsedFraction = 0f)
    }
}

@Preview(name = "Detail TopBar - Collapsed", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistDetailTopBarCollapsedPreview() {
    EssenceAppTheme {
        PlaylistDetailTopBar(title = "Mi Playlist", collapsedFraction = 1f)
    }
}
