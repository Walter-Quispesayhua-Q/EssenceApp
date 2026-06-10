package com.essence.essenceapp.feature.song.domain.usecase

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import kotlinx.coroutines.CancellationException

class GetSongUseCase(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(hlsMasterKey: String): Result<Song> {
        return try {
            val song = songRepository.syncSong(hlsMasterKey)
            if (song != null) {
                Result.success(song)
            } else {
                Result.failure(SongPlaybackUnavailableException(hlsMasterKey))
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SongNotFoundException(hlsMasterKey: String) :
    Exception("Song not found: $hlsMasterKey")

class SongPlaybackUnavailableException(hlsMasterKey: String) :
    Exception("Playable stream unavailable for: $hlsMasterKey")
