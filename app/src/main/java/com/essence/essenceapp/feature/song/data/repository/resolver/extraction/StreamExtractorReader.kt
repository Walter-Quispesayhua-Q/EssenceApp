package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

import android.util.Log
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream.PlayableStreamSelector
import javax.inject.Inject
import javax.inject.Singleton
import org.schabi.newpipe.extractor.stream.StreamExtractor

/**
 * Convierte una pagina de YouTube ya parseada por NewPipe en datos crudos de cancion.
 *
 * No elige formatos directamente: delega la seleccion de URL reproducible al
 * selector de streams, que aplica audio-only y los fallbacks permitidos. Esta
 * clase solo une esa URL con la metadata que NewPipe expone para construir
 * ExtractedSongData.
 *
 * No hace red ni reintentos. Si no hay un stream usable, devuelve Empty o
 * Incomplete para que ResilientStreamExtractor decida si corresponde intentar
 * otra pasada.
 */
@Singleton
class StreamExtractorReader @Inject constructor(
    private val streamSelector: PlayableStreamSelector
) {

    suspend fun read(hlsMasterKey: String, extractor: StreamExtractor): ExtractionResult {
        val selectedStream = streamSelector.select(hlsMasterKey, extractor)

        if (selectedStream == null) {
            return classifyMissingStream(hlsMasterKey, extractor)
        }

        Log.d(
            TAG,
            "reader[$hlsMasterKey] -> Success kind=${selectedStream.kind} " +
                    "itag=${selectedStream.itag} bitrate=${selectedStream.bitrate} " +
                    "expiresAt=${selectedStream.expiresAt}"
        )

        return ExtractionResult.Success(
            ExtractedSongData(
                hlsMasterKey = hlsMasterKey,
                title = extractor.name.orEmpty(),
                durationMs = (extractor.length * 1000).toInt(),
                uploaderName = extractor.uploaderName ?: "Unknown",
                uploaderUrl = extractor.uploaderUrl ?: "",
                thumbnailUrl = extractor.thumbnails.maxByOrNull { it.height }?.url,
                streamingUrl = selectedStream.url,
                streamingUrlExpiresAt = selectedStream.expiresAt,
                viewCount = extractor.viewCount.coerceAtLeast(0),
                releaseDate = extractor.uploadDate?.let {
                    runCatching { it.offsetDateTime().toLocalDate() }.getOrNull()
                }
            )
        )
    }

    private fun classifyMissingStream(
        hlsMasterKey: String,
        extractor: StreamExtractor
    ): ExtractionResult {
        val audioCount = runCatching { extractor.audioStreams.size }.getOrDefault(0)
        val videoCount = runCatching { extractor.videoStreams.size }.getOrDefault(0)
        val hasHls = runCatching { extractor.hlsUrl.isNotBlank() }.getOrDefault(false)

        return if (audioCount == 0 && videoCount == 0 && !hasHls) {
            Log.w(
                TAG,
                "reader[$hlsMasterKey] -> Empty " +
                        "(no audio, progressive or HLS streams in YouTube response)"
            )
            ExtractionResult.Empty
        } else {
            Log.w(
                TAG,
                "reader[$hlsMasterKey] -> Incomplete " +
                        "(streams found but none were playable: " +
                        "audio=$audioCount video=$videoCount hls=$hasHls)"
            )
            ExtractionResult.Incomplete
        }
    }

    private companion object {
        const val TAG = "StreamExtractorReader"
    }
}
