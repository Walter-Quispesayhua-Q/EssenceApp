package com.essence.essenceapp.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.SoftRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    title: String = "Perfil"
) {
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MidnightBlack.copy(alpha = 0.95f),
            tonalElevation = 2.dp
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SoftRose.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProfileTopBarPreview() {
    EssenceAppTheme {
        ProfileTopBar(title = "Walter")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProfileTopBarDefaultPreview() {
    EssenceAppTheme {
        ProfileTopBar()
    }
}