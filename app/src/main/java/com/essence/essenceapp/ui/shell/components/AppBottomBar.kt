package com.essence.essenceapp.ui.shell.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.shell.model.TopLevelDestination
import com.essence.essenceapp.ui.shell.model.TopLevelDestinations
import com.essence.essenceapp.ui.shell.shellSpring
import com.essence.essenceapp.ui.theme.GraphiteSurface
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GraphiteSurface.copy(alpha = 0.94f),
                        GraphiteSurface.copy(alpha = 0.86f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PureWhite.copy(alpha = 0.14f),
                        PureWhite.copy(alpha = 0.04f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
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
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { destination ->
            val isSelected = selectedGraphRoute == destination.graphRoute
            BottomBarItem(
                destination = destination,
                isSelected = isSelected,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: TopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = shellSpring(),
        label = "bottom_item_scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal else PureWhite.copy(alpha = 0.55f),
        animationSpec = shellSpring(),
        label = "bottom_item_icon_color"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) PureWhite else PureWhite.copy(alpha = 0.55f),
        animationSpec = shellSpring(),
        label = "bottom_item_label_color"
    )

    val pillColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal.copy(alpha = 0.18f) else MutedTeal.copy(alpha = 0f),
        animationSpec = shellSpring(),
        label = "bottom_item_pill_color"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(color = pillColor, shape = RoundedCornerShape(22.dp))
            .selectable(
                selected = isSelected,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                tint = iconColor,
                modifier = Modifier
                    .size(22.dp)
                    .scale(iconScale)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(
                    animationSpec = shellSpring(),
                    expandFrom = Alignment.Start
                ) + fadeIn(
                    animationSpec = shellSpring()
                ),
                exit = shrinkHorizontally(
                    animationSpec = shellSpring(),
                    shrinkTowards = Alignment.Start
                ) + fadeOut(
                    animationSpec = shellSpring()
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = labelColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
