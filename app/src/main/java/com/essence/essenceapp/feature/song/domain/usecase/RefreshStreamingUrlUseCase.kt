package com.essence.essenceapp.feature.song.domain.usecase

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import kotlinx.coroutines.CancellationException

class RefreshStreamingUrlUseCase(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(
        currentSong: Song,
        isStillCurrent: () -> Boolean
    ): Result<Song> {
        return try {
            val song = songRepository.refreshStreamingUrl(currentSong, isStillCurrent)
            if (song != null) {
                Result.success(song)
            } else {
                Result.failure(StreamingUrlRefreshException(currentSong.hlsMasterKey))
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class StreamingUrlRefreshException(videoId: String) :
    Exception("Failed to refresh streaming URL for: $videoId")