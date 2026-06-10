package com.essence.essenceapp.feature.playback.engine.mediacontroller

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.essence.essenceapp.feature.playback.engine.AudioPlayRequest
import javax.inject.Inject

/**
 * Convierte un pedido de audio en el MediaItem que entiende Media3.
 *
 * Mantiene la construccion de metadatos fuera del engine principal para que
 * el motor solo se encargue de reproducir y controlar el audio.
 */
class AudioMediaItemFactory @Inject constructor() {

    fun create(request: AudioPlayRequest): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(request.title)
            .setArtist(request.artistName)
            .apply {
                if (!request.artworkUri.isNullOrBlank()) {
                    setArtworkUri(Uri.parse(request.artworkUri))
                }
            }
            .build()

        return MediaItem.Builder()
            .setMediaId(request.mediaId)
            .setUri(request.url)
            .setMediaMetadata(metadata)
            .build()
    }
}