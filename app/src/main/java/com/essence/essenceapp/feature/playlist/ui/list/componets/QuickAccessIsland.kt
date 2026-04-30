package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListAction
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite

internal data class QuickAccessItem(
    val title: String,
    val value: String?,
    val icon: ImageVector,
    val tint: Color,
    val badge: String? = null,
    val action: PlaylistListAction? = null
)

internal val defaultQuickAccessItems: List<QuickAccessItem> = listOf(
    QuickAccessItem(
        title = "Historial",
        value = "Hoy",
        icon = Icons.Default.History,
        tint = MutedTeal,
        action = PlaylistListAction.OpenHistory
    ),
    QuickAccessItem(
        title = "Descargas",
        value = null,
        icon = Icons.Default.Download,
        tint = LuxeGold,
        badge = "Próximamente"
    )
)

@Composable
internal fun QuickAccessIsland(
    items: List<QuickAccessItem>,
    onAction: (PlaylistListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            count = items.size,
            key = { items[it].title }
        ) { index ->
            QuickAccessCard(
                item = items[index],
                onAction = onAction
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    item: QuickAccessItem,
    onAction: (PlaylistListAction) -> Unit
) {
    val isInteractive = item.action != null

    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(110.dp)
            .then(
                if (isInteractive) Modifier.clickable { onAction(item.action!!) }
                else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, item.tint.copy(alpha = 0.18f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            item.tint.copy(alpha = 0.16f),
                            item.tint.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = item.tint.copy(alpha = 0.18f),
                    border = BorderStroke(0.5.dp, item.tint.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.tint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    QuickAccessSubtext(item = item)
                }
            }
        }
    }
}

@Composable
private fun QuickAccessSubtext(item: QuickAccessItem) {
    when {
        item.badge != null -> {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = item.tint.copy(alpha = 0.14f),
                border = BorderStroke(0.5.dp, item.tint.copy(alpha = 0.2f))
            ) {
                Text(
                    text = item.badge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = item.tint.copy(alpha = 0.9f),
                    maxLines = 1
                )
            }
        }
        !item.value.isNullOrBlank() -> {
            Text(
                text = item.value,
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.55f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
