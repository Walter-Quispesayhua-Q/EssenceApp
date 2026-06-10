package com.essence.essenceapp.feature.song.domain.usecase

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import kotlinx.coroutines.CancellationException

/**
 * Prepara una cancion ya conocida para reproducirse con una URL vigente.
 *
 * Recibe el hlsMasterKey y el id esperado de una cancion que ya existe en
 * la API. Si la URL actual todavia sirve, devuelve esa Song. Si la URL ya
 * vencio, el repositorio usa el resolver conocido para obtener una URL
 * fresca y devolver la misma cancion lista para sonar.
 */
class RefreshStreamingUrlUseCase(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(hlsMasterKey: String, songId: Long): Result<Song> {
        return try {
            val song = songRepository.refreshStreamingUrl(hlsMasterKey, songId)
            if (song != null) {
                Result.success(song)
            } else {
                Result.failure(StreamingUrlRefreshException(hlsMasterKey))
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class StreamingUrlRefreshException(hlsMasterKey: String) :
    Exception("Failed to prepare streaming URL for: $hlsMasterKey")