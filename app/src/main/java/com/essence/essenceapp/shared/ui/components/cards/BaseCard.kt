package com.essence.essenceapp.shared.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val cardModifier =
        if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        }

    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}

@Preview(name = "Base Card", showBackground = true)
@Composable
private fun PreviewBaseCard() {
    EssenceAppTheme {
        BaseCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Contenido de ejemplo",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}