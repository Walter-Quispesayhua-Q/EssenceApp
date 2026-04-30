package com.essence.essenceapp.feature.song.domain.usecase

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import kotlinx.coroutines.CancellationException

class GetSongUseCase(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songLookup: String): Result<Song> {
        return try {
            val song = songRepository.syncSong(songLookup)
            if (song != null) {
                Result.success(song)
            } else {
                Result.failure(SongNotFoundException(songLookup))
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SongNotFoundException(songLookup: String) :
    Exception("Song not found: $songLookup")