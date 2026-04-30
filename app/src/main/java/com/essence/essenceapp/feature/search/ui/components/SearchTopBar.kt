package com.essence.essenceapp.feature.search.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

private val PillShape = RoundedCornerShape(22.dp)

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    activeTypeLabel: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val borderAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.55f else 0.10f,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "border_alpha"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.18f else 0.0f,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "glow_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0.0f to MidnightBlack,
                    0.85f to MidnightBlack.copy(alpha = 0.96f),
                    1.0f to MidnightBlack.copy(alpha = 0.0f)
                )
            )
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PillShape)
                .background(SoftRose.copy(alpha = glowAlpha))
                .padding(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PillShape)
                    .background(GraphiteSurface.copy(alpha = 0.92f))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = borderAlpha),
                                PureWhite.copy(alpha = borderAlpha * 0.35f),
                                SoftRose.copy(alpha = borderAlpha * 0.85f)
                            )
                        ),
                        shape = PillShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                0.0f to PureWhite.copy(alpha = 0.05f),
                                0.5f to Color.Transparent,
                                1.0f to Color.Black.copy(alpha = 0.10f)
                            )
                        )
                ) {
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused },
                        placeholder = {
                            Text(
                                text = "Canciones, artistas, albumes...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PureWhite.copy(alpha = 0.38f)
                            )
                        },
                        singleLine = true,
                        shape = PillShape,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = PureWhite,
                            fontWeight = FontWeight.Medium
                        ),
                        leadingIcon = {
                            SearchLeadingIcon(isFocused = isFocused)
                        },
                        trailingIcon = {
                            FadeScaleVisibility(
                                visible = query.isNotEmpty(),
                                initialScale = 0.6f
                            ) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(PureWhite.copy(alpha = 0.10f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Limpiar",
                                            tint = PureWhite.copy(alpha = 0.85f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            onSearch()
                        }),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = SoftRose
                        )
                    )
                }
            }
        }

        FadeScaleVisibility(visible = !activeTypeLabel.isNullOrBlank()) {
            ActiveFilterChip(
                label = activeTypeLabel.orEmpty(),
                modifier = Modifier.padding(top = 10.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun FadeScaleVisibility(
    visible: Boolean,
    initialScale: Float = 0.92f,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = initialScale),
        exit = fadeOut() + scaleOut(targetScale = initialScale)
    ) {
        content()
    }
}

@Composable
private fun SearchLeadingIcon(isFocused: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "icon_scale"
    )
    val tint = if (isFocused) SoftRose else PureWhite.copy(alpha = 0.55f)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (isFocused) SoftRose.copy(alpha = 0.10f) else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size((20 * scale).dp)
        )
    }
}

@Composable
private fun ActiveFilterChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SoftRose.copy(alpha = 0.14f))
            .border(
                width = 0.5.dp,
                color = SoftRose.copy(alpha = 0.35f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(SoftRose)
        )
        Text(
            text = "Buscando en: $label",
            style = MaterialTheme.typography.labelSmall,
            color = SoftRose,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(name = "SearchTopBar - Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SearchTopBarEmptyPreview() {
    EssenceAppTheme {
        SearchTopBar(query = "", onQueryChange = {}, onSearch = {})
    }
}

@Preview(name = "SearchTopBar - With Query", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SearchTopBarWithQueryPreview() {
    EssenceAppTheme {
        SearchTopBar(query = "The Weeknd", onQueryChange = {}, onSearch = {})
    }
}

@Preview(name = "SearchTopBar - Active Filter", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SearchTopBarActiveFilterPreview() {
    EssenceAppTheme {
        SearchTopBar(
            query = "Rosalia",
            onQueryChange = {},
            onSearch = {},
            activeTypeLabel = "Canciones"
        )
    }
}