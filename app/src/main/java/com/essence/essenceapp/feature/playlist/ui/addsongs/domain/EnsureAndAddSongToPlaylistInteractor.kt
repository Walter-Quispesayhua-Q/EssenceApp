package com.essence.essenceapp.feature.playlist.ui.addsongs.domain

import android.util.Log
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository
import com.essence.essenceapp.feature.playlist.ui.addsongs.extractor.AddSongMetadataExtractor
import com.essence.essenceapp.feature.playlist.ui.addsongs.extractor.toSyncRequestDTO
import com.essence.essenceapp.feature.song.data.api.SongApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException

class EnsureAndAddSongToPlaylistInteractor(
    private val playlistRepository: PlaylistRepository,
    private val songApiService: SongApiService
) {

    suspend operator fun invoke(playlistId: Long, songKey: String): Result<Boolean> {
        return try {
            ensureSongInBackend(songKey)
                ?: return Result.failure(Exception("No se pudo procesar la canción"))

            val ok = playlistRepository.addSongToPlaylist(playlistId, songKey)
            if (ok) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo agregar la canción a la playlist"))
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "ensureAndAdd failed for $songKey: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun ensureSongInBackend(songKey: String): Unit? {
        val cached = tryFetchFromBackend(songKey)
        if (cached) {
            Log.d(TAG, "GET-first hit (skip extraction): $songKey")
            return Unit
        }

        Log.d(TAG, "GET-first miss → metadata-only extraction: $songKey")
        val metadata = withTimeoutOrNull(EXTRACTION_TIMEOUT_MS) {
            AddSongMetadataExtractor.extract(songKey)
        }

        return if (metadata != null) {
            try {
                val response = songApiService.syncSong(metadata.toSyncRequestDTO())
                if (response != null) {
                    Log.d(TAG, "Sync OK for $songKey")
                    Unit
                } else {
                    Log.w(TAG, "Sync returned null for $songKey")
                    null
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed for $songKey: ${e.message}")
                null
            }
        } else {
            Log.w(TAG, "Client extraction failed, asking backend extraction as fallback: $songKey")
            try {
                songApiService.getSong(songKey, forceRefresh = true)?.let { Unit }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e(TAG, "Backend fallback failed for $songKey: ${e.message}")
                null
            }
        }
    }

    private suspend fun tryFetchFromBackend(songKey: String): Boolean {
        return try {
            songApiService.getSong(songKey, forceRefresh = false) != null
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Log.d(TAG, "Backend 404 for $songKey")
            } else {
                Log.w(TAG, "Backend HTTP ${e.code()} for $songKey: ${e.message}")
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Backend unreachable for $songKey: ${e.message}")
            false
        }
    }

    companion object {
        private const val TAG = "EnsureAddSongInter"
        private const val EXTRACTION_TIMEOUT_MS = 8_000L
    }
}
