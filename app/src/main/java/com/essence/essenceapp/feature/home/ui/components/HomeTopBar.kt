package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun HomeTopBar(
    username: String?,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit = {}
) {
    val greeting = when {
        isLoggedIn && !username.isNullOrBlank() -> "Hola, ${username.trim()}"
        isLoggedIn -> "Hola"
        else -> "Bienvenido"
    }

    val subtitle = when {
        isLoggedIn -> "¿Qué quieres escuchar hoy?"
        else -> "Inicia sesión para disfrutar más"
    }

    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MidnightBlack.copy(alpha = 0.95f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    username = username,
                    isLoggedIn = isLoggedIn
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WavingHand(isAnimated = isLoggedIn)

                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                        maxLines = 1
                    )
                }

                if (!isLoggedIn) {
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onLoginClick,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.45f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SoftRose
                        )
                    ) {
                        Text(
                            text = "Ingresar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
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

@Composable
private fun WavingHand(
    isAnimated: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isAnimated) {
        if (!isAnimated) {
            rotation.snapTo(0f)
            return@LaunchedEffect
        }
        while (true) {
            rotation.animateTo(22f, animationSpec = tween(durationMillis = 220))
            rotation.animateTo(-12f, animationSpec = tween(durationMillis = 220))
            rotation.animateTo(22f, animationSpec = tween(durationMillis = 220))
            rotation.animateTo(0f, animationSpec = tween(durationMillis = 220))
            delay(2400)
        }
    }

    Box(
        modifier = modifier.size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👋",
            fontSize = 16.sp,
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation.value
                transformOrigin = TransformOrigin(pivotFractionX = 0.7f, pivotFractionY = 0.9f)
            }
        )
    }
}

@Composable
private fun UserAvatar(
    username: String?,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val initials = username
        ?.trim()
        ?.split(" ")
        ?.take(2)
        ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
        ?.joinToString("")
        ?: ""

    Box(
        modifier = modifier.size(46.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.22f),
                                MutedTeal.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = GraphiteSurface,
            border = BorderStroke(
                1.3.dp,
                if (isLoggedIn) SoftRose.copy(alpha = 0.45f)
                else PureWhite.copy(alpha = 0.10f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoggedIn && initials.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SoftRose.copy(alpha = 0.90f),
                                        MutedTeal.copy(alpha = 0.78f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = PureWhite.copy(alpha = 0.45f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarGuestPreview() {
    EssenceAppTheme {
        HomeTopBar(username = null, isLoggedIn = false)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarLoggedPreview() {
    EssenceAppTheme {
        HomeTopBar(username = "Walter", isLoggedIn = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarLongNamePreview() {
    EssenceAppTheme {
        HomeTopBar(username = "Walter Alexander Dev", isLoggedIn = true)
    }
}