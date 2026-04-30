package com.essence.essenceapp.feature.playlist.ui.addsongs.extractor

import android.util.Log
import com.essence.essenceapp.core.extractor.NewPipeInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamExtractor

object AddSongMetadataExtractor {

    private const val TAG = "AddSongMetadataExtr"
    private const val YOUTUBE_MUSIC_BASE = "https://music.youtube.com/watch?v="

    suspend fun extract(videoId: String): AddSongMetadata? = withContext(Dispatchers.IO) {
        try {
            NewPipeInitializer.init()
            val extractor = buildStreamExtractor(videoId)
            extractor.fetchPage()

            AddSongMetadata(
                videoId = videoId,
                title = extractor.name.orEmpty(),
                durationMs = (extractor.length * 1000).toInt(),
                uploaderName = extractor.uploaderName ?: "Unknown",
                uploaderUrl = extractor.uploaderUrl ?: "",
                thumbnailUrl = extractor.thumbnails
                    .maxByOrNull { it.height }?.url,
                viewCount = extractor.viewCount.coerceAtLeast(0),
                releaseDate = extractor.uploadDate?.let {
                    try {
                        it.offsetDateTime().toLocalDate()
                    } catch (_: Exception) {
                        null
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "extract failed for $videoId: ${e.javaClass.simpleName} - ${e.message}")
            null
        }
    }

    private fun buildStreamExtractor(videoId: String): StreamExtractor {
        return ServiceList.YouTube.getStreamExtractor("$YOUTUBE_MUSIC_BASE$videoId")
    }
}
