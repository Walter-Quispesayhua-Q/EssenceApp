package com.essence.essenceapp.feature.song.data.repository.resolver.remote

import android.util.Log
import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.mapper.songToDomain
import com.essence.essenceapp.feature.song.data.repository.resolver.extraction.ExtractedSongData
import com.essence.essenceapp.feature.song.domain.model.Song
import kotlinx.coroutines.CancellationException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persiste en la api una cancion recien extraida por NewPipe.
 *
 * Hace POST /song/sync con los datos crudos y devuelve la Song ya creada
 * (con id real). Distingue entre fallo de red, respuesta vacia de la api y
 * error inesperado para que el resolver decida con esa informacion.
 */
@Singleton
class SongSyncSender @Inject constructor(
    private val apiService: SongApiService
) {

    suspend fun send(data: ExtractedSongData): RemoteResult<Song> {
        return try {
            val request = data.toApiSyncRequest()
            val song = apiService.syncSong(request)?.songToDomain()
            if (song == null) {
                Log.w(TAG, "send[${data.hlsMasterKey}] returned null body")
                RemoteResult.ApiError("POST /sync returned null body")
            } else {
                Log.d(
                    TAG,
                    "send[${data.hlsMasterKey}] OK (id=${song.id} " +
                            "hasUrl=${!song.streamingUrl.isNullOrBlank()})"
                )
                RemoteResult.Success(song)
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: IOException) {
            Log.w(
                TAG,
                "send[${data.hlsMasterKey}] network error: ${e.javaClass.simpleName} ${e.message}"
            )
            RemoteResult.NetworkError
        } catch (e: Exception) {
            Log.w(
                TAG,
                "send[${data.hlsMasterKey}] api error: ${e.javaClass.simpleName} ${e.message}"
            )
            RemoteResult.ApiError("${e.javaClass.simpleName}: ${e.message ?: "unknown"}")
        }
    }

    companion object {
        private const val TAG = "SongSyncSender"
    }
}