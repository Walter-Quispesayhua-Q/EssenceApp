package com.essence.essenceapp.ui.shell.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.shell.model.TopLevelDestination
import com.essence.essenceapp.ui.shell.model.TopLevelDestinations
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite

@Composable
fun AppBottomBar(
    selectedGraphRoute: String?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
    items: List<TopLevelDestination> = TopLevelDestinations.items
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        BottomBarGlassContainer {
            BottomBarContent(
                items = items,
                selectedGraphRoute = selectedGraphRoute,
                onDestinationSelected = onDestinationSelected
            )
        }
    }
}

@Composable
private fun BottomBarGlassContainer(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        color = GraphiteSurface.copy(alpha = 0.88f),
        tonalElevation = 0.dp,
        shadowElevation = 10.dp
    ) {
        content()
    }
}

@Composable
private fun BottomBarContent(
    items: List<TopLevelDestination>,
    selectedGraphRoute: String?,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { destination ->
            val isSelected = selectedGraphRoute == destination.graphRoute

            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MutedTeal,
                    selectedTextColor = PureWhite,
                    indicatorColor = MutedTeal.copy(alpha = 0.20f),
                    unselectedIconColor = PureWhite.copy(alpha = 0.62f),
                    unselectedTextColor = PureWhite.copy(alpha = 0.62f)
                )
            )
        }
    }
}