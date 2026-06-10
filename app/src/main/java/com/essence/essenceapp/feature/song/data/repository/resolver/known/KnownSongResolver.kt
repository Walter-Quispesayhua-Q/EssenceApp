package com.essence.essenceapp.feature.song.data.repository.resolver.known

import android.util.Log
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlReachabilityValidator
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlValidator
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractedSongData
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractedSongDataCache
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractionResult
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ResilientStreamExtractor
import com.essence.essenceapp.feature.song.data.repository.resolver.remote.RemoteResult
import com.essence.essenceapp.feature.song.data.repository.resolver.remote.SongFetcher
import com.essence.essenceapp.feature.song.data.repository.resolver.remote.StreamingUrlRefresher
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.resolver.SongResolveError
import com.essence.essenceapp.feature.song.domain.resolver.SongResolveResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resuelve una cancion que ya existe en la api.
 *
 * Usa la Song guardada como base. Si su URL ya no sirve, obtiene una URL
 * fresca con NewPipe y la mezcla sobre esa misma Song, manteniendo el id
 * y la metadata de la api. No crea canciones nuevas.
 */
@Singleton
class KnownSongResolver @Inject constructor(
    private val songFetcher: SongFetcher,
    private val resilientExtractor: ResilientStreamExtractor,
    private val urlRefresher: StreamingUrlRefresher,
    private val extractionCache: ExtractedSongDataCache,
    private val urlValidator: StreamingUrlValidator,
    private val reachabilityValidator: StreamingUrlReachabilityValidator
) {

    suspend fun resolve(hlsMasterKey: String, expectedSongId: Long): SongResolveResult {
        val key = hlsMasterKey.trim()
        if (key.isBlank()) {
            Log.w(TAG, "resolve called without hlsMasterKey")
            return SongResolveResult.Failure(SongResolveError.MissingHlsMasterKey)
        }
        if (expectedSongId <= 0L) {
            Log.w(TAG, "resolve called with invalid expectedSongId=$expectedSongId")
            return SongResolveResult.Failure(
                SongResolveError.Unknown("invalid expectedSongId: $expectedSongId")
            )
        }

        Log.d(TAG, "resolve[$key] start (expectedSongId=$expectedSongId)")

        val apiSong = when (val fetched = songFetcher.fetch(key)) {
            is RemoteResult.Success -> fetched.value ?: run {
                Log.w(TAG, "resolve[$key] song not found in api")
                return SongResolveResult.Failure(
                    SongResolveError.BackendFailed("song not found in api")
                )
            }
            RemoteResult.NetworkError -> {
                Log.w(TAG, "resolve[$key] fetch failed by network")
                return SongResolveResult.Failure(
                    SongResolveError.NetworkFailed("network error during GET song")
                )
            }
            is RemoteResult.ApiError -> {
                Log.w(TAG, "resolve[$key] fetch failed by api: ${fetched.message}")
                return SongResolveResult.Failure(
                    SongResolveError.BackendFailed(fetched.message)
                )
            }
        }

        if (apiSong.id != expectedSongId) {
            Log.w(
                TAG,
                "resolve[$key] id mismatch: expected=$expectedSongId got=${apiSong.id}"
            )
        }

        if (urlValidator.isFresh(apiSong.streamingUrl, apiSong.streamingUrlExpiresAt)) {
            if (reachabilityValidator.isReachable(apiSong.streamingUrl)) {
                Log.d(TAG, "resolve[$key] api URL still valid and reachable, returning")
                return SongResolveResult.Success(apiSong)
            }

            Log.w(TAG, "resolve[$key] api URL is fresh but unreachable, refreshing via extractor")
        } else {
            Log.d(TAG, "resolve[$key] api URL expired/empty, refreshing via extractor")
        }

        return refreshWithExtractor(key, apiSong)
    }

    private suspend fun refreshWithExtractor(
        hlsMasterKey: String,
        apiSong: Song
    ): SongResolveResult {
        val data = obtainExtractedData(hlsMasterKey)
            ?: return SongResolveResult.Failure(
                SongResolveError.ExtractorFailed(
                    "could not obtain fresh URL (extractor Empty or Incomplete)"
                )
            )

        validateMetadataMatch(apiSong, data)

        notifyApiOfFreshUrl(hlsMasterKey, data)

        val finalSong = apiSong.withExtractedStream(data)
        if (!urlValidator.isFresh(finalSong.streamingUrl, finalSong.streamingUrlExpiresAt)) {
            Log.w(TAG, "refresh[$hlsMasterKey] final Song still has no playable URL")
            return SongResolveResult.Failure(
                SongResolveError.UrlInvalid("URL became invalid after merging with api song")
            )
        }

        Log.d(TAG, "refresh[$hlsMasterKey] success (id=${finalSong.id})")
        return SongResolveResult.Success(finalSong)
    }

    private fun validateMetadataMatch(apiSong: Song, data: ExtractedSongData) {
        val durationDiff = Math.abs(apiSong.durationMs - data.durationMs)
        if (durationDiff > 20_000) {
            Log.w(
                TAG,
                "metadata mismatch expectedSongId=${apiSong.id} durationDiffMs=$durationDiff " +
                        "apiTitle=\"${apiSong.title}\" apiDurationMs=${apiSong.durationMs} " +
                        "ytTitle=\"${data.title}\" ytDurationMs=${data.durationMs} " +
                        "videoId=${data.hlsMasterKey}"
            )
        }
    }

    private suspend fun obtainExtractedData(hlsMasterKey: String): ExtractedSongData? {
        extractionCache.get(hlsMasterKey)?.let { cached ->
            if (cached.hasUsableStreamingUrl(source = "cache")) {
                Log.d(TAG, "refresh[$hlsMasterKey] cache HIT")
                return cached
            }

            Log.w(TAG, "refresh[$hlsMasterKey] cache HIT but URL is not usable, running extractor")
        }
        Log.d(TAG, "refresh[$hlsMasterKey] cache MISS, running extractor")
        return when (val result = resilientExtractor.extract(hlsMasterKey)) {
            is ExtractionResult.Success -> {
                result.data
                    .takeIf { it.hasUsableStreamingUrl(source = "extractor", skipReachability = true) }
                    ?.also(extractionCache::put)
            }
            ExtractionResult.Empty -> {
                Log.w(TAG, "refresh[$hlsMasterKey] extractor returned Empty")
                null
            }
            ExtractionResult.Incomplete -> {
                Log.w(TAG, "refresh[$hlsMasterKey] extractor returned Incomplete")
                null
            }
        }
    }

    private suspend fun ExtractedSongData.hasUsableStreamingUrl(source: String, skipReachability: Boolean = false): Boolean {
        if (!urlValidator.isFresh(streamingUrl, streamingUrlExpiresAt)) {
            Log.w(TAG, "refresh[$hlsMasterKey] $source URL not fresh enough")
            return false
        }

        if (!skipReachability && !reachabilityValidator.isReachable(streamingUrl)) {
            Log.w(TAG, "refresh[$hlsMasterKey] $source URL is not reachable")
            return false
        }

        return true
    }

    private suspend fun notifyApiOfFreshUrl(
        hlsMasterKey: String,
        data: ExtractedSongData
    ) {
        val url = data.streamingUrl ?: return
        when (val patch = urlRefresher.refresh(hlsMasterKey, url, data.streamingUrlExpiresAt)) {
            is RemoteResult.Success -> Unit
            RemoteResult.NetworkError -> {
                Log.w(TAG, "refresh[$hlsMasterKey] PATCH failed by network, continuing anyway")
            }
            is RemoteResult.ApiError -> {
                Log.w(
                    TAG,
                    "refresh[$hlsMasterKey] PATCH failed by api: ${patch.message}, " +
                        "continuing anyway"
                )
            }
        }
    }

    private fun Song.withExtractedStream(data: ExtractedSongData): Song = copy(
        streamingUrl = data.streamingUrl,
        streamingUrlExpiresAt = data.streamingUrlExpiresAt
    )

    companion object {
        private const val TAG = "KnownSongResolver"
    }
}
