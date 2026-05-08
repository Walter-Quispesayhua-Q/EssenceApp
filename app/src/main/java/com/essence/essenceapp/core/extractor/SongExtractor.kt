package com.essence.essenceapp.core.extractor

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.stream.StreamExtractor
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

object SongExtractor {

    private const val TAG = "SongExtractor"
    private const val YOUTUBE_MUSIC_BASE = "https://music.youtube.com/watch?v="

    private const val CACHE_TTL_MS = 5 * 60 * 1000L
    private const val MAX_CACHE_ENTRIES = 64
    private const val MAX_RETRIES = 1
    private const val INITIAL_BACKOFF_MS = 250L

    private data class CachedSong(val value: ExtractedSong, val timestampMs: Long)
    private data class CachedUrl(val value: String, val timestampMs: Long)

    private val songCache = ConcurrentHashMap<String, CachedSong>()
    private val urlCache = ConcurrentHashMap<String, CachedUrl>()

    suspend fun extract(videoId: String): ExtractedSong? = withContext(Dispatchers.IO) {
        cachedSongIfFresh(videoId)?.let { return@withContext it }
        try {
            NewPipeInitializer.init()
            val extractor = withRetry { buildAndFetch(videoId) }
            val extracted = ExtractedSong(
                videoId = videoId,
                title = extractor.name.orEmpty(),
                durationMs = (extractor.length * 1000).toInt(),
                uploaderName = extractor.uploaderName ?: "Unknown",
                uploaderUrl = extractor.uploaderUrl ?: "",
                thumbnailUrl = extractor.thumbnails.maxByOrNull { it.height }?.url,
                streamingUrl = extractor.audioStreams.maxByOrNull { it.bitrate }?.url,
                viewCount = extractor.viewCount.coerceAtLeast(0),
                releaseDate = extractor.uploadDate?.let {
                    try {
                        it.offsetDateTime().toLocalDate()
                    } catch (_: Exception) {
                        null
                    }
                }
            )
            putSongCache(videoId, extracted)
            extracted
        } catch (e: Throwable) {
            Log.e(TAG, "extract failed for $videoId: ${e.javaClass.simpleName} - ${e.message}")
            null
        }
    }

    suspend fun extractStreamingUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        cachedUrlIfFresh(videoId)?.let { return@withContext it }
        try {
            NewPipeInitializer.init()
            val extractor = withRetry { buildAndFetch(videoId) }
            val url = extractor.audioStreams.maxByOrNull { it.bitrate }?.url
            if (url != null) putUrlCache(videoId, url)
            url
        } catch (e: Throwable) {
            Log.e(TAG, "extractStreamingUrl failed for $videoId: ${e.javaClass.simpleName} - ${e.message}")
            null
        }
    }

    fun invalidate(videoId: String) {
        songCache.remove(videoId)
        urlCache.remove(videoId)
    }

    private fun buildAndFetch(videoId: String): StreamExtractor {
        val extractor = ServiceList.YouTube.getStreamExtractor("$YOUTUBE_MUSIC_BASE$videoId")
        extractor.fetchPage()
        return extractor
    }

    private suspend fun <T> withRetry(block: () -> T): T {
        var lastError: Throwable? = null
        var delayMs = INITIAL_BACKOFF_MS
        var attempt = 0
        while (attempt <= MAX_RETRIES) {
            try {
                return block()
            } catch (e: ReCaptchaException) {
                lastError = e
            } catch (e: IOException) {
                lastError = e
            }
            attempt++
            if (attempt <= MAX_RETRIES) {
                delay(delayMs)
                delayMs *= 2
            }
        }
        throw lastError ?: IllegalStateException("retry without error")
    }

    private fun cachedSongIfFresh(videoId: String): ExtractedSong? {
        val entry = songCache[videoId] ?: return null
        return if (isFresh(entry.timestampMs)) {
            entry.value
        } else {
            songCache.remove(videoId)
            null
        }
    }

    private fun cachedUrlIfFresh(videoId: String): String? {
        val entry = urlCache[videoId] ?: return null
        return if (isFresh(entry.timestampMs)) {
            entry.value
        } else {
            urlCache.remove(videoId)
            null
        }
    }

    private fun isFresh(timestampMs: Long): Boolean =
        System.currentTimeMillis() - timestampMs <= CACHE_TTL_MS

    private fun putSongCache(videoId: String, song: ExtractedSong) {
        if (songCache.size >= MAX_CACHE_ENTRIES) songCache.clear()
        songCache[videoId] = CachedSong(song, System.currentTimeMillis())
    }

    private fun putUrlCache(videoId: String, url: String) {
        if (urlCache.size >= MAX_CACHE_ENTRIES) urlCache.clear()
        urlCache[videoId] = CachedUrl(url, System.currentTimeMillis())
    }
}