package com.essence.essenceapp.feature.song.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose

private const val AMBIENT_BLUR_DP = 40
private const val AMBIENT_SCALE = 1.15f
private const val AMBIENT_IMAGE_ALPHA = 0.55f
private const val AMBIENT_SATURATION = 1.15f

@Composable
fun SongAmbientBackground(
    imageKey: String?,
    modifier: Modifier = Modifier
) {
    val imageUrl = resolveImageUrl(imageKey)
    val supportsBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        if (imageUrl != null) {
            val imageModifier = if (supportsBlur) {
                Modifier
                    .matchParentSize()
                    .scale(AMBIENT_SCALE)
                    .blur(
                        radius = AMBIENT_BLUR_DP.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
            } else {
                Modifier
                    .matchParentSize()
                    .scale(AMBIENT_SCALE)
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
                alpha = AMBIENT_IMAGE_ALPHA,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply { setToSaturation(AMBIENT_SATURATION) }
                )
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.22f),
                                MutedTeal.copy(alpha = 0.14f),
                                MidnightBlack
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MidnightBlack.copy(alpha = 0.35f),
                            MidnightBlack.copy(alpha = 0.70f),
                            MidnightBlack.copy(alpha = 0.92f),
                            MidnightBlack
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            MidnightBlack.copy(alpha = 0.45f)
                        ),
                        radius = 1600f
                    )
                )
        )
    }
}
