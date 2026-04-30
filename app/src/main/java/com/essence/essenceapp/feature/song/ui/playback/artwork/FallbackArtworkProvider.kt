package com.essence.essenceapp.feature.song.ui.playback.artwork

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.essence.essenceapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FallbackArtwork"
private const val ARTWORK_SIZE_PX = 512
private const val LOGO_PADDING_RATIO = 0.18f
private const val BACKGROUND_COLOR_HEX = "#121212"

@Singleton
class FallbackArtworkProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val bytes: ByteArray by lazy { buildArtworkBytes() }

    private fun buildArtworkBytes(): ByteArray {
        return try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_logo_essence)
                ?: run {
                    Log.w(TAG, "ic_logo_essence no disponible")
                    return ByteArray(0)
                }

            val bitmap = Bitmap.createBitmap(
                ARTWORK_SIZE_PX,
                ARTWORK_SIZE_PX,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.parseColor(BACKGROUND_COLOR_HEX))

            val padding = (ARTWORK_SIZE_PX * LOGO_PADDING_RATIO).toInt()
            drawable.setBounds(
                padding,
                padding,
                ARTWORK_SIZE_PX - padding,
                ARTWORK_SIZE_PX - padding
            )
            drawable.draw(canvas)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            bitmap.recycle()
            stream.toByteArray()
        } catch (e: Exception) {
            Log.w(TAG, "Error generando artwork fallback", e)
            ByteArray(0)
        }
    }
}
