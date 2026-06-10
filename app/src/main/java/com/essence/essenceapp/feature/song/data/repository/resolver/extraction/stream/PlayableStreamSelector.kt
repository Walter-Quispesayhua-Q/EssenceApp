package com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream

import android.util.Log
import com.essence.essenceapp.core.extractor.youtube.protocol.YoutubeClientPolicy
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlReachability
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlReachabilityValidator
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull
import org.schabi.newpipe.extractor.stream.StreamExtractor

/**
 * Selecciona la mejor URL reproducible desde una pagina ya parseada por NewPipe.
 *
 * Lanza una carrera controlada entre HLS, audio-only y progressive: los tres
 * arrancan en paralelo, pero progressive espera un breve delay de gracia para
 * dar prioridad a audio-only (menor consumo de datos, mejor calidad). Si
 * progressive gana la carrera, una ventana de prioridad permite que audio-only
 * lo alcance si llega poco despues.
 *
 * Cada candidato se valida por HTTP antes de aceptarlo, descartando URLs que
 * YouTube ya rechaza con 403.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PlayableStreamSelector @Inject constructor(
    private val policy: YoutubeClientPolicy,
    private val audioOnlyStrategy: AudioOnlyStreamStrategy,
    private val progressiveStrategy: ProgressiveStreamStrategy,
    private val hlsStrategy: HlsStreamStrategy,
    private val reachabilityValidator: StreamingUrlReachabilityValidator
) {

    private companion object {
        const val TAG = "PlayableStreamSelector"
        const val PROGRESSIVE_GRACE_MS = 150L
        const val PRIORITY_WINDOW_MS = 150L
        const val RACE_TIMEOUT_MS = 4_500L
        const val RACE_CACHE_TTL_MS = 30_000L
    }

    private data class CachedRace(
        val result: ExtractedPlayableStream,
        val timestamp: Long
    )

    private val raceCache = java.util.concurrent.ConcurrentHashMap<String, CachedRace>()

    private sealed interface LaneResult {
        data class Found(val stream: ExtractedPlayableStream) : LaneResult
        data object Empty : LaneResult
    }

    suspend fun select(
        hlsMasterKey: String,
        extractor: StreamExtractor
    ): ExtractedPlayableStream? {
        raceCache[hlsMasterKey]?.let { cached ->
            if (System.currentTimeMillis() - cached.timestamp < RACE_CACHE_TTL_MS) {
                Log.d(TAG, "race[$hlsMasterKey] cache HIT (skipping duplicate race)")
                return cached.result
            }
            raceCache.remove(hlsMasterKey)
        }

        Log.d(TAG, "race[$hlsMasterKey] start")
        val raceStartMs = System.currentTimeMillis()

        val result = runRace(hlsMasterKey, extractor)

        val elapsed = System.currentTimeMillis() - raceStartMs
        if (result != null) {
            raceCache[hlsMasterKey] = CachedRace(result, System.currentTimeMillis())
            Log.d(
                TAG,
                "race[$hlsMasterKey] winner kind=${result.kind} " +
                        "itag=${result.itag} in ${elapsed}ms"
            )
        } else {
            Log.w(TAG, "race[$hlsMasterKey] no reachable stream in ${elapsed}ms")
        }

        return result
    }

    private suspend fun runRace(
        hlsMasterKey: String,
        extractor: StreamExtractor
    ): ExtractedPlayableStream? = coroutineScope {

        val hlsLane = async {
            if (!policy.hlsFallbackEnabled) {
                Log.d(TAG, "lane[$hlsMasterKey] HLS disabled")
                return@async LaneResult.Empty
            }
            runLane(hlsMasterKey, "HLS", hlsStrategy.candidates(hlsMasterKey, extractor))
        }

        val audioLane = async {
            runLane(
                hlsMasterKey, "AUDIO",
                audioOnlyStrategy.candidates(hlsMasterKey, extractor.audioStreams)
            )
        }

        val progressiveLane = async {
            if (!policy.progressiveFallbackEnabled) {
                Log.d(TAG, "lane[$hlsMasterKey] PROGRESSIVE disabled")
                return@async LaneResult.Empty
            }
            delay(PROGRESSIVE_GRACE_MS.milliseconds)
            Log.d(TAG, "lane[$hlsMasterKey] PROGRESSIVE grace elapsed, starting")
            runLane(
                hlsMasterKey, "PROGRESSIVE",
                progressiveStrategy.candidates(hlsMasterKey, extractor.videoStreams)
            )
        }

        try {
            withTimeoutOrNull(RACE_TIMEOUT_MS.milliseconds) {
                val firstWinner = selectFirstFound(hlsLane, audioLane, progressiveLane)
                    ?: return@withTimeoutOrNull null

                if (firstWinner.kind != ExtractedStreamKind.PROGRESSIVE) {
                    cancelAll(hlsLane, audioLane, progressiveLane)
                    return@withTimeoutOrNull firstWinner
                }
                Log.d(
                    TAG,
                    "race[$hlsMasterKey] PROGRESSIVE arrived first, " +
                            "waiting ${PRIORITY_WINDOW_MS}ms for audio/hls"
                )

                val betterStream = waitForBetterStream(
                    hlsMasterKey, hlsLane, audioLane
                )

                cancelAll(hlsLane, audioLane, progressiveLane)

                betterStream ?: firstWinner
            }
        } catch (ce: CancellationException) {
            hlsLane.cancel()
            audioLane.cancel()
            progressiveLane.cancel()
            throw ce
        }
    }

    private suspend fun selectFirstFound(
        vararg lanes: Deferred<LaneResult>
    ): ExtractedPlayableStream? {
        val remaining = lanes.toMutableSet()

        while (remaining.isNotEmpty()) {
            val result = select {
                for (lane in remaining) {
                    lane.onAwait { it }
                }
            }

            when (result) {
                is LaneResult.Found -> return result.stream
                LaneResult.Empty -> {
                    remaining.removeAll { it.isCompleted }
                }
            }
        }

        return null
    }

    private suspend fun waitForBetterStream(
        hlsMasterKey: String,
        hlsLane: Deferred<LaneResult>,
        audioLane: Deferred<LaneResult>
    ): ExtractedPlayableStream? {
        if (hlsLane.isCompleted && audioLane.isCompleted) {
            return extractFound(hlsLane) ?: extractFound(audioLane)
        }

        return withTimeoutOrNull(PRIORITY_WINDOW_MS.milliseconds) {
            val pending = listOf(hlsLane, audioLane).filter { !it.isCompleted }
            if (pending.isEmpty()) return@withTimeoutOrNull null

            for (lane in pending) {
                val result = lane.await()
                if (result is LaneResult.Found) {
                    Log.d(
                        TAG,
                        "race[$hlsMasterKey] ${result.stream.kind} arrived " +
                                "in priority window, upgrading from PROGRESSIVE"
                    )
                    return@withTimeoutOrNull result.stream
                }
            }

            null
        }
    }

    private fun extractFound(
        lane: Deferred<LaneResult>
    ): ExtractedPlayableStream? {
        if (!lane.isCompleted) return null
        val result = runCatching { lane.getCompleted() }.getOrNull()
        return (result as? LaneResult.Found)?.stream
    }

    private fun cancelAll(vararg lanes: Deferred<*>) {
        lanes.forEach { runCatching { it.cancel() } }
    }

    private suspend fun runLane(
        hlsMasterKey: String,
        laneName: String,
        candidates: List<ExtractedPlayableStream>
    ): LaneResult {
        if (candidates.isEmpty()) {
            Log.d(TAG, "lane[$hlsMasterKey] $laneName no candidates")
            return LaneResult.Empty
        }

        Log.d(TAG, "lane[$hlsMasterKey] $laneName ${candidates.size} candidates")

        for (candidate in candidates) {
            val result = reachabilityValidator.validate(candidate.url)
            if (result.isReachable) {
                Log.d(
                    TAG,
                    "lane[$hlsMasterKey] $laneName reachable " +
                            "itag=${candidate.itag} ${result.describe()}"
                )
                return LaneResult.Found(candidate)
            }

            Log.w(
                TAG,
                "lane[$hlsMasterKey] $laneName skip " +
                        "itag=${candidate.itag} ${result.describe()}"
            )
        }

        Log.d(TAG, "lane[$hlsMasterKey] $laneName all rejected")
        return LaneResult.Empty
    }

    private fun StreamingUrlReachability.describe(): String = when (this) {
        is StreamingUrlReachability.Reachable -> "code=$code"
        is StreamingUrlReachability.Rejected -> "rejected=$code"
        is StreamingUrlReachability.Failed -> "failed=${error.javaClass.simpleName}"
        is StreamingUrlReachability.Invalid -> "invalid=$reason"
    }
}
