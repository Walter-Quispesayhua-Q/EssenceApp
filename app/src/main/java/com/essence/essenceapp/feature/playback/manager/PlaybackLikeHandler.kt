package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.playback.domain.PlaybackError
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Maneja el like de la cancion actual.
 *
 * Playback no es el dueno real del like. Solo delega al modulo song y refleja
 * el cambio en nowPlaying para que la UI responda rapido.
 */
@Singleton
class PlaybackLikeHandler @Inject constructor(
    private val stateStore: PlaybackStateStore,
    private val addLikeSongUseCase: AddLikeSongUseCase,
    private val deleteLikeSongUseCase: DeleteLikeSongUseCase,
    @ApplicationScope private val scope: CoroutineScope
) {

    fun toggleLike(songId: Long) {
        val currentItem = stateStore.currentItem ?: return
        val currentSong = stateStore.resolvedSong
        if (currentItem.songId != songId && currentSong?.id != songId) return

        val currentlyLiked = stateStore.nowPlaying.value?.isLiked == true

        scope.launch {
            val result = if (currentlyLiked) {
                deleteLikeSongUseCase(songId)
            } else {
                addLikeSongUseCase(songId)
            }

            result.onSuccess {
                if (stateStore.currentItem?.songId == songId ||
                    stateStore.resolvedSong?.id == songId
                ) {
                    val resolvedSong = stateStore.resolvedSong
                    if (resolvedSong != null) {
                        stateStore.setCurrentResolvedSong(
                            resolvedSong.copy(isLiked = !currentlyLiked)
                        )
                    } else {
                        stateStore.updateNowPlaying(isLikedOverride = !currentlyLiked)
                    }
                }
            }

            result.onFailure { error ->
                stateStore.fail(
                    PlaybackError.Unknown(
                        message = error.message ?: "No se pudo actualizar el like.",
                        cause = error
                    )
                )
            }
        }
    }

    fun setCurrentLike(songId: Long, isLiked: Boolean) {
        val currentItem = stateStore.currentItem ?: return
        val currentSong = stateStore.resolvedSong
        if (currentItem.songId != songId && currentSong?.id != songId) return

        if (currentSong != null) {
            stateStore.setCurrentResolvedSong(currentSong.copy(isLiked = isLiked))
        } else {
            stateStore.updateNowPlaying(isLikedOverride = isLiked)
        }
    }
}