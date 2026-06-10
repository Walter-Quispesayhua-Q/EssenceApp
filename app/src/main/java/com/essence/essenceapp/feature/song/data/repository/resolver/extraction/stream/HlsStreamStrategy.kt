package com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream

import android.util.Log
import com.essence.essenceapp.core.streaming.ExtractorTimeStreamUrl
import javax.inject.Inject
import javax.inject.Singleton
import org.schabi.newpipe.extractor.stream.StreamExtractor

/**
 * Usa el manifest HLS expuesto por NewPipe como fallback avanzado.
 *
 * HLS suele ser mas relevante para lives o respuestas donde YouTube no entrega
 * streams directos tradicionales. Por eso no es el camino principal: solo se
 * intenta cuando la politica de seleccion lo permite.
 */
@Singleton
class HlsStreamStrategy @Inject constructor() {

    fun candidates(
        hlsMasterKey: String,
        extractor: StreamExtractor
    ): List<ExtractedPlayableStream> {
        val hlsUrl = runCatching { extractor.hlsUrl }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: run {
                Log.d(TAG, "hls[$hlsMasterKey] unavailable")
                return emptyList()
            }

        Log.d(TAG, "hls[$hlsMasterKey] candidate manifest")

        return listOf(
            ExtractedPlayableStream(
                kind = ExtractedStreamKind.HLS,
                url = hlsUrl,
                expiresAt = ExtractorTimeStreamUrl.expireFrom(hlsUrl)
            )
        )
    }

    private companion object {
        const val TAG = "HlsStreamStrategy"
    }
}
