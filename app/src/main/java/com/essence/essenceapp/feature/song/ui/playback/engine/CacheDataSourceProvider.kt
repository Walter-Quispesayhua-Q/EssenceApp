package com.essence.essenceapp.feature.song.ui.playback.engine

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import com.essence.essenceapp.core.network.ApiConstants
import com.essence.essenceapp.core.storage.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PROVIDER_USER_AGENT = "EssenceAppPlayer"
private const val CONNECT_TIMEOUT_MS = 25_000
private const val READ_TIMEOUT_MS = 45_000

/**
 * Arma el pipeline de [DataSource.Factory] que el [ExoPlayerFactory]
 * inyecta en cada instancia de [androidx.media3.exoplayer.ExoPlayer].
 *
 * ## Pipeline (de fuera hacia dentro)
 *
 * ```
 *   CacheDataSource            ← primer nivel: consulta el SimpleCache
 *       │  (FLAG_IGNORE_CACHE_ON_ERROR)
 *       ▼
 *   DefaultDataSource.Factory  ← despacha según esquema (http/file/asset)
 *       │
 *       ▼
 *   ResolvingDataSource        ← decora la request con `Authorization`
 *       │  cuando corresponde
 *       ▼
 *   DefaultHttpDataSource      ← ejecuta la request HTTP real
 * ```
 *
 * ## Inyección de auth *just-in-time*
 *
 * El token no se adjunta en `createFactory`: se resuelve dentro del
 * lambda de [ResolvingDataSource] cada vez que se pide un
 * [androidx.media3.datasource.DataSpec]. Así, si el token se refresca
 * durante una sesión larga, la siguiente petición del player ya lleva
 * el nuevo Bearer sin recrear el pipeline.
 *
 * ## Seguridad: filtrado por host
 *
 * [shouldAttachAuthHeader] compara el host del stream con el host del
 * backend propio. Sólo se adjunta el `Authorization` si coinciden. Esto
 * impide filtrar el Bearer a CDNs de terceros (p. ej. `googlevideo.com`),
 * cuyas URLs llevan su propia firma y no necesitan el token.
 *
 * ## Timeouts
 *
 * - [CONNECT_TIMEOUT_MS] 25 s: holgura suficiente para TLS + DNS en
 *   redes móviles lentas.
 * - [READ_TIMEOUT_MS] 45 s: el throughput de los segmentos HLS de
 *   audio es bajo; 45 s da margen para recuperar un chunk aun con
 *   degradación puntual de la red antes de que el `LoadErrorHandlingPolicy`
 *   del factory decida reintentar.
 */
@Singleton
@OptIn(UnstableApi::class)
class CacheDataSourceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager,
    private val mediaAudioCache: MediaAudioCache
) {

    /**
     * Crea un [DataSource.Factory] nuevo por cada player. El pipeline
     * resultante primero busca en el cache y sólo va a red en miss.
     *
     * @param useAuthHeader si `true`, la fábrica inyecta el header
     *   `Authorization: Bearer <token>` en cada request saliente. Para
     *   determinar este flag de forma segura, usar [shouldAttachAuthHeader].
     */
    fun createFactory(useAuthHeader: Boolean): DataSource.Factory {
        val httpFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(PROVIDER_USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(CONNECT_TIMEOUT_MS)
            .setReadTimeoutMs(READ_TIMEOUT_MS)

        val resolvingFactory: DataSource.Factory =
            ResolvingDataSource.Factory(httpFactory) { dataSpec ->
                if (!useAuthHeader) {
                    dataSpec
                } else {
                    val token = tokenManager.getCachedToken()
                    if (token.isNullOrBlank()) {
                        dataSpec
                    } else {
                        dataSpec.buildUpon()
                            .setHttpRequestHeaders(
                                dataSpec.httpRequestHeaders + mapOf(
                                    "Authorization" to "Bearer $token"
                                )
                            )
                            .build()
                    }
                }
            }

        val upstreamDataSourceFactory = DefaultDataSource.Factory(context, resolvingFactory)

        return CacheDataSource.Factory()
            .setCache(mediaAudioCache.cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            // FLAG_IGNORE_CACHE_ON_ERROR: si los ficheros de cache están
            // corruptos o la DB de metadata falla, caer a red en vez de
            // abortar la reproducción.
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    /**
     * Determina si una URL apunta al backend propio y, por tanto, si
     * sus requests deben llevar el `Authorization: Bearer`.
     *
     * Compara host (case-insensitive) entre la URL del stream y
     * [ApiConstants.BASE_URL]. Los streams externos
     * (p. ej. `googlevideo.com` para contenido de NewPipe) devuelven
     * `false` y no reciben el token nunca.
     */
    fun shouldAttachAuthHeader(url: String): Boolean {
        val apiHost = Uri.parse(ApiConstants.BASE_URL).host ?: return false
        val streamHost = Uri.parse(url).host ?: return false
        return apiHost.equals(streamHost, ignoreCase = true)
    }
}
