package com.essence.essenceapp.shared.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    shape: Shape = RoundedCornerShape(22.dp),
    accent: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactiveModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    Surface(
        modifier = interactiveModifier,
        shape = shape,
        color = GraphiteSurface.copy(alpha = 0.55f),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.clip(shape)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.06f),
                                PureWhite.copy(alpha = 0.02f),
                                Color.Transparent
                            )
                        )
                    )
            )

            if (accent != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.10f),
                                    Color.Transparent
                                ),
                                radius = 600f
                            )
                        )
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.16f),
                                PureWhite.copy(alpha = 0.04f)
                            )
                        ),
                        shape = shape
                    )
            )

            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun BasePreview() {
    EssenceAppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                PreviewBody()
            }

            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                accent = SoftRose
            ) {
                PreviewBody()
            }

            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                accent = LuxeGold
            ) {
                PreviewBody()
            }

            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                accent = MutedTeal
            ) {
                PreviewBody()
            }
        }
    }
}

@Composable
private fun PreviewBody() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    )
}