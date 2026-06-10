package com.essence.essenceapp.feature.song.data.repository.resolver.unknown

import android.util.Log
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlReachabilityValidator
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlValidator
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractedSongData
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractedSongDataCache
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractionResult
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ResilientStreamExtractor
import com.essence.essenceapp.feature.song.data.repository.resolver.remote.RemoteResult
import com.essence.essenceapp.feature.song.data.repository.resolver.remote.SongSyncSender
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.resolver.SongResolveError
import com.essence.essenceapp.feature.song.domain.resolver.SongResolveResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resuelve una cancion que llega sin songId usando NewPipe y la guarda en la api.
 *
 * Es el camino para items que solo tienen hlsMasterKey: extrae los datos,
 * los sincroniza con la api y devuelve la Song final con una URL fresca
 * lista para reproducir. Tambien aprovecha un cache en memoria para
 * evitar relanzar el extractor cuando los datos siguen siendo validos.
 */
@Singleton
class UnknownSongResolver @Inject constructor(
    private val resilientExtractor: ResilientStreamExtractor,
    private val syncSender: SongSyncSender,
    private val extractionCache: ExtractedSongDataCache,
    private val urlValidator: StreamingUrlValidator,
    private val reachabilityValidator: StreamingUrlReachabilityValidator
) {

    suspend fun resolve(hlsMasterKey: String): SongResolveResult {
        val key = hlsMasterKey.trim()
        if (key.isBlank()) {
            Log.w(TAG, "resolve called without hlsMasterKey")
            return SongResolveResult.Failure(SongResolveError.MissingHlsMasterKey)
        }

        Log.d(TAG, "resolve[$key] start")

        extractionCache.get(key)?.let { cached ->
            if (cached.hasUsableStreamingUrl(source = "cache")) {
                Log.d(TAG, "resolve[$key] cache HIT, persisting cached data")
                return persistAndBuild(cached, source = "cache")
            }

            Log.w(TAG, "resolve[$key] cache HIT but URL is not usable, running extractor")
        }
        Log.d(TAG, "resolve[$key] cache MISS, running extractor")

        return when (val result = resilientExtractor.extract(key)) {
            is ExtractionResult.Success -> {
                if (!result.data.hasUsableStreamingUrl(source = "extractor")) {
                    return SongResolveResult.Failure(
                        SongResolveError.UrlInvalid("extractor URL is not reachable")
                    )
                }

                extractionCache.put(result.data)
                persistAndBuild(result.data, source = "extractor")
            }
            ExtractionResult.Empty -> {
                Log.w(TAG, "resolve[$key] extractor returned Empty")
                SongResolveResult.Failure(
                    SongResolveError.ExtractorFailed("no audio in YouTube response")
                )
            }
            ExtractionResult.Incomplete -> {
                Log.w(TAG, "resolve[$key] extractor returned Incomplete")
                SongResolveResult.Failure(
                    SongResolveError.ExtractorFailed("incomplete extraction after retries")
                )
            }
        }
    }

    private suspend fun persistAndBuild(
        data: ExtractedSongData,
        source: String
    ): SongResolveResult {
        val synced = when (val sendResult = syncSender.send(data)) {
            is RemoteResult.Success -> sendResult.value
            RemoteResult.NetworkError -> {
                Log.w(TAG, "persist[${data.hlsMasterKey}] sync failed by network")
                return SongResolveResult.Failure(
                    SongResolveError.NetworkFailed("network error during POST /sync")
                )
            }
            is RemoteResult.ApiError -> {
                Log.w(
                    TAG,
                    "persist[${data.hlsMasterKey}] sync failed by api: ${sendResult.message}"
                )
                return SongResolveResult.Failure(
                    SongResolveError.BackendFailed(sendResult.message)
                )
            }
        }

        val finalSong = synced.withExtractedStream(data)
        if (!urlValidator.isFresh(finalSong.streamingUrl, finalSong.streamingUrlExpiresAt)) {
            Log.w(TAG, "persist[${data.hlsMasterKey}] final Song still has no playable URL")
            return SongResolveResult.Failure(
                SongResolveError.UrlInvalid("URL became invalid after merging with sync result")
            )
        }

        Log.d(TAG, "persist[${data.hlsMasterKey}] success from $source (id=${finalSong.id})")
        return SongResolveResult.Success(finalSong)
    }

    private suspend fun ExtractedSongData.hasUsableStreamingUrl(source: String): Boolean {
        if (!urlValidator.isFresh(streamingUrl, streamingUrlExpiresAt)) {
            Log.w(TAG, "persist[$hlsMasterKey] $source URL not fresh enough")
            return false
        }

        if (!reachabilityValidator.isReachable(streamingUrl)) {
            Log.w(TAG, "persist[$hlsMasterKey] $source URL is not reachable")
            return false
        }

        return true
    }

    private fun Song.withExtractedStream(data: ExtractedSongData): Song = copy(
        streamingUrl = data.streamingUrl,
        streamingUrlExpiresAt = data.streamingUrlExpiresAt
    )

    companion object {
        private const val TAG = "UnknownSongResolver"
    }
}
