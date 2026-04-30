package com.essence.essenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.R
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.ui.theme.LuxeGold

@Composable
fun LogoIsland(
    modifier: Modifier = Modifier,
    accent: Color = LuxeGold,
    logoSize: Dp = 96.dp,
    accentAlpha: Float = 0.10f,
    materialAlpha: Float = 0.55f,
    contentPadding: PaddingValues = PaddingValues(24.dp)
) {
    GlassIsland(
        modifier = modifier,
        accent = accent,
        accentAlpha = accentAlpha,
        materialAlpha = materialAlpha,
        showInnerShadow = false,
        contentPadding = contentPadding
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_essence),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(logoSize)
        )
    }
}
