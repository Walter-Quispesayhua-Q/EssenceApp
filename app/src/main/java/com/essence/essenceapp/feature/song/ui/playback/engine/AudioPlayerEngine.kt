package com.essence.essenceapp.feature.song.ui.playback.engine

import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.essence.essenceapp.feature.song.ui.playback.AudioPlayerState
import com.essence.essenceapp.feature.song.ui.playback.PlaybackRepeatMode
import com.essence.essenceapp.feature.song.ui.playback.artwork.FallbackArtworkProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val AUDIO_TAG = "AUDIO_DEBUG"
private const val MAX_RETRY_COUNT = 3
private const val RETRY_DELAY_MS = 1_000L

/**
 * Wrapper de alto nivel sobre [ExoPlayer] que expone la reproduccion como
 * un [StateFlow] de [AudioPlayerState] reactivo.
 *
 * ## Responsabilidades
 *
 * - **Lifecycle del [ExoPlayer]**: crea, libera y recicla la instancia
 *   cuando cambia la necesidad de auth (mismo player se reusa entre
 *   canciones del mismo dominio para evitar el coste de recrearlo).
 * - **Estado reactivo**: traduce los callbacks imperativos del Player a
 *   un [AudioPlayerState] inmutable que el resto del sistema consume.
 * - **Recuperacion de errores**: distingue entre errores transitorios
 *   (red caida, timeout) y permanentes (URL expirada 403/410, parsing).
 *   Reintenta los transitorios con backoff hasta MAX_RETRY_COUNT veces.
 * - **Refresh request**: cuando detecta URL expirada, expone el flag
 *   [AudioPlayerState.requiresSourceRefresh] para que el [PlaybackManager]
 *   dispare un refresh con NewPipe + PATCH al backend.
 * - **Fade-in**: aplica una rampa de volumen ~200ms al arranque para
 *   suavizar el transitorio inicial del decoder/AudioTrack (evita el
 *   "raspado" del primer sample).
 * - **Position tracking**: mientras esta reproduciendo, emite la posicion
 *   cada 500ms al StateFlow para que la UI (slider, MiniPlayer) anime.
 *
 * ## Modelo de errores
 *
 * Tres niveles, ordenados por prioridad:
 *
 * 1. **URL expirada (403/410)**: el backend pre-firma URLs con TTL ~5h.
 *    Si una sesion dura mas de eso, las URLs expiran. No reintenta el
 *    request (siempre fallaria), sino que setea `requiresSourceRefresh`
 *    y deja al PlaybackManager que llame a [RefreshStreamingUrlUseCase].
 * 2. **Errores IO transitorios**: red caida, DNS fallo, timeout. Maximo
 *    [MAX_RETRY_COUNT] reintentos con [RETRY_DELAY_MS] de delay.
 * 3. **Errores permanentes**: parsing, container corrupto, decoder. No
 *    reintenta. Reporta `errorMessage` y deja al UI mostrarlo.
 *
 * ## Singleton + Hilt
 *
 * Una unica instancia compartida entre el [PlaybackManager] (UI) y el
 * [com.essence.essenceapp.feature.song.ui.playback.service.MediaPlaybackService]
 * (background, lock screen, BT). Asi ambos ven el mismo player y
 * estado.
 */
@Singleton
class AudioPlayerEngine @Inject constructor(
    private val exoPlayerFactory: ExoPlayerFactory,
    private val fallbackArtworkProvider: FallbackArtworkProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var player: ExoPlayer? = null
    private var positionJob: Job? = null
    private var fadeInJob: Job? = null
    private var currentUrl: String? = null
    private var currentUsesAuthHeader: Boolean = false
    private var repeatMode: PlaybackRepeatMode = PlaybackRepeatMode.Off
    private var retryCount: Int = 0

    fun getPlayerOrNull(): ExoPlayer? = player

    /**
     * Reproduce el audio en [url], creando o reusando un [ExoPlayer].
     *
     * Comportamiento:
     * - Si ya estabamos reproduciendo la misma URL (`currentUrl == url`)
     *   y `forceRestart=false`, solo asegura `playWhenReady=true` y
     *   retorna sin reiniciar (preserva la posicion actual).
     * - Si cambia el host (auth requerida vs no), libera el player
     *   anterior y crea uno nuevo con la configuracion adecuada.
     * - En todos los casos, resetea [retryCount] a 0: cada cambio de
     *   cancion es un nuevo "presupuesto" de reintentos.
     *
     * @param url URL del stream (HLS master, progressive MP3, etc).
     * @param forceRestart si true, reinicia incluso cuando ya estabamos en
     *   esa URL. Util cuando se hizo refresh de la URL y la nueva tiene
     *   un signature distinto pero la misma cancion.
     * @param title artista,artworkUri,mediaId metadata para [MediaSession]
     *   (notification, lock screen, BT).
     */
    fun play(
        url: String,
        forceRestart: Boolean = false,
        title: String? = null,
        artist: String? = null,
        artworkUri: String? = null,
        mediaId: String? = null
    ) {
        val useAuthHeader = exoPlayerFactory.shouldAttachAuthHeader(url)
        val exo = getOrCreatePlayer(useAuthHeader)

        if (!forceRestart && currentUrl == url && exo.currentMediaItem != null) {
            exo.playWhenReady = true
            updateState()
            return
        }

        currentUrl = url
        retryCount = 0
        Log.d(AUDIO_TAG, "play url=$url useAuthHeader=$useAuthHeader")

        _state.value = _state.value.copy(
            isBuffering = true,
            errorMessage = null,
            currentUrl = url,
            repeatMode = repeatMode,
            requiresSourceRefresh = false
        )

        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .apply {
                if (!artworkUri.isNullOrBlank()) {
                    setArtworkUri(Uri.parse(artworkUri))
                } else {
                    val fallbackBytes = fallbackArtworkProvider.bytes
                    if (fallbackBytes.isNotEmpty()) {
                        setArtworkData(fallbackBytes, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                    }
                }
            }
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(mediaId ?: url)
            .setUri(url)
            .setMediaMetadata(metadata)
            .build()

        exo.setMediaItem(mediaItem)
        exo.prepare()
        exo.playWhenReady = true
        updateState()
    }

    fun resume() {
        fadeInJob?.cancel()
        fadeInJob = null
        val exo = player ?: return
        exo.volume = 1f
        when (exo.playbackState) {
            Player.STATE_IDLE -> {
                exo.prepare()
                exo.playWhenReady = true
            }
            Player.STATE_ENDED -> {
                exo.seekTo(0L)
                exo.playWhenReady = true
            }
            else -> {
                exo.playWhenReady = true
            }
        }
        _state.value = _state.value.copy(errorMessage = null)
        updateState()
    }

    fun pause() {
        fadeInJob?.cancel()
        fadeInJob = null
        player?.let {
            it.volume = 1f
            it.playWhenReady = false
        }
        updateState()
    }

    fun stop() {
        stopPositionTracking()
        fadeInJob?.cancel()
        fadeInJob = null
        player?.stop()
        player?.clearMediaItems()
        currentUrl = null
        _state.value = AudioPlayerState(repeatMode = repeatMode)
    }

    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
        _state.value = _state.value.copy(positionMs = positionMs, repeatMode = repeatMode)
    }

    fun isPlayingUrl(url: String?): Boolean {
        return !url.isNullOrBlank() && currentUrl == url
    }

    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            PlaybackRepeatMode.Off -> PlaybackRepeatMode.One
            PlaybackRepeatMode.One -> PlaybackRepeatMode.Off
        }

        player?.repeatMode = repeatMode.toExoRepeatMode()
        updateState()
    }

    fun release() {
        stop()
        player?.release()
        player = null
    }

    /**
     * Fade-in suave (~200ms) para suavizar el transitorio inicial del
     * AudioTrack/decoder. No soluciona la raíz, pero enmascara el
     * "click" o "raspado" de arranque sin perder contenido audible.
     */
    private fun fadeInPlayer(exo: ExoPlayer) {
        fadeInJob?.cancel()
        fadeInJob = scope.launch {
            exo.volume = 0f
            repeat(10) { step ->
                exo.volume = (step + 1) / 10f
                delay(20L)
            }
        }
    }

    /**
     * Solo reintentar errores IO/network transitorios.
     * NO reintentar errores de parsing, container o decoder — esos
     * son problemas estructurales, no transitorios.
     */
    private fun isRecoverable(error: PlaybackException): Boolean {
        return error.errorCode in setOf(
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
            PlaybackException.ERROR_CODE_IO_UNSPECIFIED
        )
    }

    private fun isExpiredStreamError(error: PlaybackException): Boolean {
        if (error.errorCode != PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) return false
        val httpError = error.cause as? HttpDataSource.InvalidResponseCodeException ?: return false
        return httpError.responseCode in setOf(403, 410)
    }

    fun clearSourceRefreshRequest() {
        _state.value = _state.value.copy(
            requiresSourceRefresh = false,
            errorMessage = null
        )
    }

    private fun getOrCreatePlayer(useAuthHeader: Boolean): ExoPlayer {
        val existing = player
        if (existing != null && currentUsesAuthHeader == useAuthHeader) {
            return existing
        }

        stopPositionTracking()
        fadeInJob?.cancel()
        player?.release()

        return exoPlayerFactory.create(useAuthHeader).also { exo ->
            player = exo
            currentUsesAuthHeader = useAuthHeader
            exo.repeatMode = repeatMode.toExoRepeatMode()

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateState()

                    // Fade-in al arranque para suavizar transitorio inicial
                    if (playbackState == Player.STATE_READY &&
                        exo.playWhenReady &&
                        exo.currentPosition < 250L
                    ) {
                        fadeInPlayer(exo)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateState()
                    if (isPlaying) {
                        // Playback recuperado: resetear el contador de
                        // reintentos para que futuros errores transitorios
                        // (en la misma cancion) puedan reintentarse de
                        // nuevo desde cero.
                        retryCount = 0
                        startPositionTracking()
                    } else {
                        stopPositionTracking()
                    }
                }

                override fun onRepeatModeChanged(repeatModeValue: Int) {
                    repeatMode = when (repeatModeValue) {
                        Player.REPEAT_MODE_ONE -> PlaybackRepeatMode.One
                        else -> PlaybackRepeatMode.Off
                    }
                    updateState()
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(
                        AUDIO_TAG,
                        "ExoPlayer error: ${error.errorCodeName} ${error.message} " +
                                "(retry $retryCount/$MAX_RETRY_COUNT)",
                        error
                    )

                    if (isExpiredStreamError(error)) {
                        Log.d(AUDIO_TAG, "Expired stream detected, requesting source refresh")
                        _state.value = _state.value.copy(
                            isPlaying = false,
                            isBuffering = false,
                            repeatMode = repeatMode,
                            errorMessage = "La URL del audio expiró.",
                            requiresSourceRefresh = true
                        )
                        return
                    }

                    if (isRecoverable(error) && retryCount < MAX_RETRY_COUNT) {
                        retryCount++
                        Log.d(AUDIO_TAG, "Retrying playback… attempt $retryCount")
                        scope.launch {
                            delay(RETRY_DELAY_MS)
                            // Usar la propiedad `player` actual en vez del
                            // `exo` capturado: si entre el error y el retry
                            // se cambio el player (por cambio de auth), el
                            // viejo ya fue released y `exo.prepare()` lanzaria
                            // IllegalStateException.
                            player?.prepare()
                            player?.playWhenReady = true
                        }
                    } else {
                        _state.value = _state.value.copy(
                            isPlaying = false,
                            isBuffering = false,
                            repeatMode = repeatMode,
                            errorMessage = error.message ?: error.errorCodeName
                        )
                    }
                }
            })
        }
    }

    private fun updateState() {
        val exo = player ?: run {
            _state.value = _state.value.copy(
                currentUrl = currentUrl,
                repeatMode = repeatMode
            )
            return
        }

        val ended = exo.playbackState == Player.STATE_ENDED
        val previous = _state.value

        _state.value = AudioPlayerState(
            isPlaying = exo.isPlaying,
            isBuffering = exo.playbackState == Player.STATE_BUFFERING,
            positionMs = exo.currentPosition.coerceAtLeast(0L),
            durationMs = exo.duration.coerceAtLeast(0L),
            currentUrl = currentUrl,
            repeatMode = repeatMode,
            errorMessage = previous.errorMessage,
            hasEnded = ended,
            requiresSourceRefresh = previous.requiresSourceRefresh
        )
    }

    private fun startPositionTracking() {
        stopPositionTracking()

        positionJob = scope.launch {
            while (isActive) {
                val exo = player ?: break

                _state.value = _state.value.copy(
                    positionMs = exo.currentPosition.coerceAtLeast(0L),
                    durationMs = exo.duration.coerceAtLeast(0L),
                    currentUrl = currentUrl,
                    repeatMode = repeatMode
                )

                delay(500L)
            }
        }
    }

    private fun stopPositionTracking() {
        positionJob?.cancel()
        positionJob = null
    }

    private fun PlaybackRepeatMode.toExoRepeatMode(): Int =
        when (this) {
            PlaybackRepeatMode.Off -> Player.REPEAT_MODE_OFF
            PlaybackRepeatMode.One -> Player.REPEAT_MODE_ONE
        }
}