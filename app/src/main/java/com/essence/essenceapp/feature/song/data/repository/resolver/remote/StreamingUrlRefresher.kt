package com.essence.essenceapp.feature.song.data.repository.resolver.remote

import android.util.Log
import com.essence.essenceapp.feature.song.data.api.SongApiService
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Avisa a la api de una URL de streaming nueva ya extraida en el cliente.
 *
 * Hace un PATCH al endpoint de la cancion con la URL fresca y su tiempo
 * de expiracion. Devuelve si la api acepto la actualizacion o por que no
 * pudo hacerlo. No vuelve a leer la cancion despues; la respuesta es solo
 * un confirm.
 */
@Singleton
class StreamingUrlRefresher @Inject constructor(
    private val apiService: SongApiService
) {

    suspend fun refresh(
        hlsMasterKey: String,
        streamingUrl: String,
        expiresAt: Instant?
    ): RemoteResult<Unit> {
        return try {
            val response = apiService.refreshStreamingUrl(hlsMasterKey, streamingUrl, expiresAt)
            if (response.isSuccessful) {
                Log.d(TAG, "refresh[$hlsMasterKey] OK (expiresAt=$expiresAt)")
                RemoteResult.Success(Unit)
            } else {
                Log.w(
                    TAG,
                    "refresh[$hlsMasterKey] api rejected (code=${response.code()})"
                )
                RemoteResult.ApiError("PATCH rejected with code ${response.code()}")
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: IOException) {
            Log.w(
                TAG,
                "refresh[$hlsMasterKey] network error: ${e.javaClass.simpleName} ${e.message}"
            )
            RemoteResult.NetworkError
        } catch (e: Exception) {
            Log.w(
                TAG,
                "refresh[$hlsMasterKey] api error: ${e.javaClass.simpleName} ${e.message}"
            )
            RemoteResult.ApiError("${e.javaClass.simpleName}: ${e.message ?: "unknown"}")
        }
    }

    companion object {
        private const val TAG = "StreamingUrlRefresher"
    }
}