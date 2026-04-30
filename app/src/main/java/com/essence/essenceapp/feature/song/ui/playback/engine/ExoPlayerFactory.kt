package com.essence.essenceapp.feature.song.ui.playback.engine

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.exoplayer.util.EventLogger
import com.essence.essenceapp.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val AUDIO_TAG = "AUDIO_DEBUG"

/**
 * Fabrica de instancias de [ExoPlayer] configuradas para reproducir audio
 * por streaming HTTP/HLS con auth, cache en disco y recuperacion de errores.
 *
 * ## Pipeline de datos (de fuera hacia dentro)
 *
 * ```
 *   ExoPlayer
 *     |
 *     V
 *   DefaultMediaSourceFactory  ── parsea HLS / progressive / DASH
 *     |
 *     V
 *   CacheDataSource            ── primero busca en disco (LRU 100MB)
 *     |
 *     V
 *   DefaultDataSource          ── despacho por esquema (file / asset / http)
 *     |
 *     V
 *   ResolvingDataSource        ── inyecta Authorization Bearer just-in-time
 *     |
 *     V
 *   DefaultHttpDataSource      ── HTTP real (OkHttp-style)
 * ```
 *
 * ## Decisiones clave
 *
 * - **Auth just-in-time** ([ResolvingDataSource]): el token se inyecta solo
 *   cuando el host del stream es el mismo que el del backend. Asi nunca
 *   filtramos el Bearer a S3/CloudFront por accidente.
 * - **Cache en disco antes que red**: una cancion ya escuchada arranca de
 *   forma INSTANTANEA porque el rango de bytes esta en disco.
 *   `FLAG_IGNORE_CACHE_ON_ERROR` cae a network sin fallar si el cache esta
 *   corrupto.
 * - **Retries con backoff**: 500ms, 1.5s, 3s, despues no mas retries. Solo
 *   errores IO (red), no errores de parsing (esos son estructurales).
 * - **Buffer conservador**: 20s minimo / 50s maximo. Empezamos a reproducir
 *   a los 2.5s de buffer (rapido), y rebufferizamos a 5s tras un stall.
 * - **WAKE_MODE_NETWORK**: mantiene CPU/wifi despiertos durante streaming
 *   en background. ExoPlayer toma el wakelock automaticamente.
 * - **EventLogger en DEBUG only**: en release no inunda logcat ni gasta CPU.
 *
 * ## Lifecycle
 *
 * Singleton. Crea un nuevo [ExoPlayer] en cada llamada a [create]. La
 * decision de cuando recrearlo (cuando cambia useAuthHeader) la toma el
 * [AudioPlayerEngine].
 */
@Singleton
@OptIn(UnstableApi::class)
class ExoPlayerFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheDataSourceProvider: CacheDataSourceProvider
) {

    /**
     * Crea una instancia de [ExoPlayer] lista para reproducir audio.
     *
     * @param useAuthHeader cuando es true, inyecta el header `Authorization:
     *   Bearer <token>` en cada peticion HTTP del player. Solo deberia ser
     *   true cuando el host del stream coincide con el del backend (ver
     *   [shouldAttachAuthHeader]).
     *
     * Construye el pipeline en este orden:
     *  1. **HTTP base** con timeouts agresivos (20s connect, 30s read).
     *  2. **Resolving** que decora cada [androidx.media3.datasource.DataSpec]
     *     con headers de auth si corresponde.
     *  3. **Cache** sobre el upstream (lee disco primero, descarga al miss).
     *  4. **MediaSourceFactory** con politica de retry IO solamente.
     *  5. **LoadControl** con buffer conservador (ver constantes).
     *  6. **Renderers** con fallback a decoder software y queueing async.
     *  7. **AudioAttributes** declarativos para que el sistema gestione
     *     audio focus, ducking automatico y enrutamiento a salida actual.
     *
     * El [ExoPlayer] resultante esta en estado IDLE y debe recibir un
     * [androidx.media3.common.MediaItem] + `prepare()` para empezar a
     * cargar contenido.
     */
    fun create(useAuthHeader: Boolean): ExoPlayer {
        val dataSourceFactory = cacheDataSourceProvider.createFactory(useAuthHeader)

        // ── Retry de red con backoff (solo errores IO, no parsing) ──
        val loadErrorPolicy: LoadErrorHandlingPolicy =
            object : DefaultLoadErrorHandlingPolicy() {
                override fun getRetryDelayMsFor(
                    loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo
                ): Long {
                    return when (loadErrorInfo.errorCount) {
                        1 -> 500L
                        2 -> 1_500L
                        3 -> 3_000L
                        else -> C.TIME_UNSET // no más retries
                    }
                }
            }

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setLoadErrorHandlingPolicy(loadErrorPolicy)

        // ── Buffer conservador para audio (doc recomienda 1000/2000 como mínimo) ──
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                20_000,  // minBufferMs
                50_000,  // maxBufferMs
                2_500,   // bufferForPlaybackMs
                5_000    // bufferForPlaybackAfterRebufferMs
            )
            .build()

        // ── Audio attributes para focus y duck ──
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        // ── Renderers con fallback a software decoder ──
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableAudioFloatOutput(false)
            .setEnableDecoderFallback(true)
            .forceEnableMediaCodecAsynchronousQueueing()

        return ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .apply {
                skipSilenceEnabled = false
                installDiagnostics()
            }
    }

    fun shouldAttachAuthHeader(url: String): Boolean =
        cacheDataSourceProvider.shouldAttachAuthHeader(url)

    /**
     * Instala instrumentacion de diagnostico sobre el [ExoPlayer].
     *
     * - [EventLogger] de Media3: imprime estado del pipeline (carga,
     *   buffer, errores, formatos). MUY verboso (~50-100 logs por
     *   cancion). Solo activo en builds DEBUG para no inflar logcat ni
     *   gastar CPU en release.
     * - Listener custom: imprime transiciones de estado (IDLE/BUFFERING/
     *   READY/ENDED) y cambios de isPlaying con la posicion actual.
     *
     * Para depurar en runtime:
     * ```
     * adb logcat EventLogger:* AUDIO_DEBUG:* *:s
     * ```
     */
    private fun ExoPlayer.installDiagnostics() {
        if (BuildConfig.DEBUG) {
            addAnalyticsListener(EventLogger())
        }

        addAnalyticsListener(object : AnalyticsListener {
            override fun onAudioSessionIdChanged(
                eventTime: AnalyticsListener.EventTime,
                audioSessionId: Int
            ) {
                Log.d(AUDIO_TAG, "audioSessionId=$audioSessionId")
            }
        })

        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateName = when (playbackState) {
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> "READY"
                    Player.STATE_ENDED -> "ENDED"
                    else -> "UNKNOWN($playbackState)"
                }
                Log.d(AUDIO_TAG, "state=$stateName pos=${currentPosition}ms")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(AUDIO_TAG, "isPlaying=$isPlaying pos=${currentPosition}ms")
            }
        })
    }
}