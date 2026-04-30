package com.essence.essenceapp.core.extractor

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamExtractor
import org.schabi.newpipe.extractor.stream.StreamInfo

object SongExtractor {

    private const val TAG = "SongExtractor"
    private const val YOUTUBE_MUSIC_BASE = "https://music.youtube.com/watch?v="

    suspend fun extract(videoId: String): ExtractedSong? = withContext(Dispatchers.IO) {
        try {
            NewPipeInitializer.init()
            val info = getStreamInfo(videoId)

            ExtractedSong(
                videoId = videoId,
                title = info.name,
                durationMs = (info.duration * 1000).toInt(),
                uploaderName = info.uploaderName ?: "Unknown",
                uploaderUrl = info.uploaderUrl ?: "",
                thumbnailUrl = info.thumbnails
                    .maxByOrNull { it.height }?.url,
                streamingUrl = extractBestAudioUrl(info),
                viewCount = if (info.viewCount >= 0) info.viewCount else 0,
                releaseDate = info.uploadDate?.let {
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

    suspend fun extractStreamingUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            NewPipeInitializer.init()
            val extractor = buildStreamExtractor(videoId)
            extractor.fetchPage()
            extractor.audioStreams
                .maxByOrNull { it.bitrate }
                ?.url
        } catch (e: Throwable) {
            Log.e(TAG, "extractStreamingUrl failed for $videoId: ${e.javaClass.simpleName} - ${e.message}")
            null
        }
    }

    private fun buildStreamExtractor(videoId: String): StreamExtractor {
        return ServiceList.YouTube.getStreamExtractor("$YOUTUBE_MUSIC_BASE$videoId")
    }

    private fun getStreamInfo(videoId: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, "$YOUTUBE_MUSIC_BASE$videoId")
    }

    private fun extractBestAudioUrl(info: StreamInfo): String? {
        return info.audioStreams
            .maxByOrNull { it.bitrate }
            ?.url
    }
}