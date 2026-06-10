package com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream

import android.util.Log
import com.essence.essenceapp.core.streaming.ExtractorTimeStreamUrl
import javax.inject.Inject
import javax.inject.Singleton
import org.schabi.newpipe.extractor.stream.VideoStream

/**
 * Ordena streams progressive/muxed cuando no hay audio-only reproducible.
 *
 * Progressive trae audio y video juntos. No es el camino ideal para musica
 * porque consume mas datos, pero permite mantener reproduccion cuando YouTube
 * no expone streams de audio separados.
 */
@Singleton
class ProgressiveStreamStrategy @Inject constructor() {

    fun candidates(
        hlsMasterKey: String,
        streams: List<VideoStream>
    ): List<ExtractedPlayableStream> {
        Log.d(TAG, "progressive[$hlsMasterKey] streams=${streams.size}")

        val playable = streams.filter {
            it.isUrl &&
                    it.content.isNotBlank() &&
                    !it.isVideoOnly
        }.sortedWith(
            compareByDescending<VideoStream> { it.progressivePreference() }
                .thenByDescending { it.bitrate }
        )

        Log.d(TAG, "progressive[$hlsMasterKey] playable=${playable.size}/${streams.size}")

        return playable.map { stream ->
            Log.d(
                TAG,
                "progressive[$hlsMasterKey] candidate itag=${stream.itag} " +
                        "bitrate=${stream.bitrate} resolution=${stream.resolution} " +
                        "format=${stream.format?.name}"
            )

            ExtractedPlayableStream(
                kind = ExtractedStreamKind.PROGRESSIVE,
                url = stream.content,
                expiresAt = ExtractorTimeStreamUrl.expireFrom(stream.content),
                itag = stream.itag,
                bitrate = stream.bitrate,
                mimeType = stream.format?.mimeType
            )
        }
    }

    private fun VideoStream.progressivePreference(): Int = when (itag) {
        18 -> 50  // mp4 360p, suele ser el progressive mas comun y compatible
        22 -> 45  // mp4 720p, si existe consume mas datos pero es usable
        17 -> 30  // 3gp 144p, legacy fallback
        36 -> 25  // 3gp 240p, legacy fallback
        else -> 10
    }

    private companion object {
        const val TAG = "ProgressiveStreamStrategy"
    }
}
