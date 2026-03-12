package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailTopBar(
    title: String,
    collapsedFraction: Float = 1f,
    onBack: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onMore: () -> Unit = {}
) {
    val fraction = collapsedFraction.coerceIn(0f, 1f)
    val containerColor = lerp(
        Color.Transparent,
        MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
        fraction
    )

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
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
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Preview(name = "Artist TopBar - Expanded", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArtistDetailTopBarExpandedPreview() {
    EssenceAppTheme {
        ArtistDetailTopBar(
            title = "Bad Bunny",
            collapsedFraction = 0f
        )
    }
}

@Preview(name = "Artist TopBar - Collapsed", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArtistDetailTopBarCollapsedPreview() {
    EssenceAppTheme {
        ArtistDetailTopBar(
            title = "Bad Bunny",
            collapsedFraction = 1f
        )
    }
}
