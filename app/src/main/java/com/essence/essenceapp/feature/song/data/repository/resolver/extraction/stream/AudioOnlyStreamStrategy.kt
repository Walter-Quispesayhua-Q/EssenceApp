package com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream

import android.util.Log
import com.essence.essenceapp.core.streaming.ExtractorTimeStreamUrl
import javax.inject.Inject
import javax.inject.Singleton
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.AudioTrackType

/**
 * Ordena los streams audio-only expuestos por NewPipe.
 *
 * Es el camino principal para musica: evita descargar video, prioriza pistas
 * originales cuando YouTube entrega varias variantes y usa bitrate como
 * desempate entre formatos reproducibles.
 */
@Singleton
class AudioOnlyStreamStrategy @Inject constructor() {

    fun candidates(
        hlsMasterKey: String,
        streams: List<AudioStream>
    ): List<ExtractedPlayableStream> {
        Log.d(TAG, "audio[$hlsMasterKey] streams=${streams.size}")

        val playable = streams.filter { it.isUrl && it.content.isNotBlank() }
        Log.d(TAG, "audio[$hlsMasterKey] playable=${playable.size}/${streams.size}")

        val candidates = playable.preferOriginalAudioTracks(hlsMasterKey)
            .sortedWith(
                compareByDescending<AudioStream> { it.streamPreference() }
                    .thenByDescending { it.averageBitrate.coerceAtLeast(it.bitrate) }
            )

        Log.d(TAG, "audio[$hlsMasterKey] candidates=${candidates.size}")

        return candidates.map { stream ->
            val bitrate = stream.averageBitrate.coerceAtLeast(stream.bitrate)
            Log.d(
                TAG,
                "audio[$hlsMasterKey] candidate itag=${stream.itag} " +
                        "bitrate=$bitrate trackType=${stream.audioTrackType} " +
                        "locale=${stream.audioLocale}"
            )

            ExtractedPlayableStream(
                kind = ExtractedStreamKind.AUDIO_ONLY,
                url = stream.content,
                expiresAt = ExtractorTimeStreamUrl.expireFrom(stream.content),
                itag = stream.itag,
                bitrate = bitrate
            )
        }
    }

    private fun List<AudioStream>.preferOriginalAudioTracks(
        hlsMasterKey: String
    ): List<AudioStream> {
        if (isEmpty()) return this

        val originalTracks = filter { it.audioTrackType == AudioTrackType.ORIGINAL }
        return when {
            originalTracks.size == size -> this
            originalTracks.isNotEmpty() -> {
                Log.d(
                    TAG,
                    "audio[$hlsMasterKey] preferring original tracks " +
                            "${originalTracks.size}/$size"
                )
                originalTracks
            }
            else -> {
                Log.d(TAG, "audio[$hlsMasterKey] no original track found")
                this
            }
        }
    }

    private fun AudioStream.streamPreference(): Int = when (itag) {
        251 -> 50  // opus 160 kbps
        140 -> 45  // m4a 128 kbps
        250 -> 40  // opus 70 kbps
        249 -> 35  // opus 50 kbps
        141 -> 30  // m4a 256 kbps, raro/deprecado
        else -> 10
    }

    private companion object {
        const val TAG = "AudioOnlyStreamStrategy"
    }
}
