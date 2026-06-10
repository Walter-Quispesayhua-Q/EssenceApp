package com.essence.essenceapp.feature.song.data.repository.resolver.remote

import android.util.Log
import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.mapper.songToDomain
import com.essence.essenceapp.feature.song.domain.model.Song
import kotlinx.coroutines.CancellationException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Busca una cancion ya guardada en la api usando su hlsMasterKey.
 *
 * Solo consulta datos existentes. No extrae audio, no crea canciones y no
 * decide si la URL sirve para reproducir; esa validacion la hace el resolver.
 *
 * Devuelve Success(null) cuando la api respondio bien pero no encontro la
 * cancion, distinto de un fallo de red o de un error de la api.
 */
@Singleton
class SongFetcher @Inject constructor(
    private val apiService: SongApiService
) {

    suspend fun fetch(hlsMasterKey: String): RemoteResult<Song?> {
        return try {
            val song = apiService.getSong(hlsMasterKey)?.songToDomain()
            if (song == null) {
                Log.d(TAG, "fetch[$hlsMasterKey] not found")
            } else {
                Log.d(
                    TAG,
                    "fetch[$hlsMasterKey] OK (id=${song.id} " +
                            "hasUrl=${!song.streamingUrl.isNullOrBlank()})"
                )
            }
            RemoteResult.Success(song)
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: IOException) {
            Log.w(
                TAG,
                "fetch[$hlsMasterKey] network error: ${e.javaClass.simpleName} ${e.message}"
            )
            RemoteResult.NetworkError
        } catch (e: Exception) {
            Log.w(
                TAG,
                "fetch[$hlsMasterKey] api error: ${e.javaClass.simpleName} ${e.message}"
            )
            RemoteResult.ApiError("${e.javaClass.simpleName}: ${e.message ?: "unknown"}")
        }
    }

    companion object {
        private const val TAG = "SongFetcher"
    }
}