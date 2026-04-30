package com.essence.essenceapp.shared.ui.components.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.PureWhite

@Composable
fun RankBadge(
    position: Int,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
    fontSize: TextUnit = 12.sp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GraphiteSurface.copy(alpha = 0.85f),
                            GraphiteSurface.copy(alpha = 0.55f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        Text(
            text = position.toString(),
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = PureWhite.copy(alpha = 0.85f)
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun RankBadgesPreview() {
    EssenceAppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..10).forEach { position ->
                    RankBadge(position = position)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RankBadge(position = 1, size = 48.dp, fontSize = 20.sp)
                RankBadge(position = 2, size = 48.dp, fontSize = 20.sp)
                RankBadge(position = 3, size = 48.dp, fontSize = 20.sp)
                RankBadge(position = 4, size = 48.dp, fontSize = 20.sp)
            }
        }
    }
}
