package com.essence.essenceapp.shared.ui.components.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import com.essence.essenceapp.ui.theme.PureWhite

@Composable
fun GuestPlaybackHint(
    modifier: Modifier = Modifier,
    text: String = "Inicia sesión para reproducir"
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontStyle = FontStyle.Italic,
        color = PureWhite.copy(alpha = 0.55f),
        modifier = modifier
    )
}
