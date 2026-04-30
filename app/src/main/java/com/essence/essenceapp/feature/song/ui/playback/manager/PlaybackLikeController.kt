package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_LIKE"

@Singleton
class PlaybackLikeController @Inject constructor(
    private val addLikeSongUseCase: AddLikeSongUseCase,
    private val deleteLikeSongUseCase: DeleteLikeSongUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    fun setLiked(isLiked: Boolean) {
        _isLiked.value = isLiked
    }

    fun toggleLike(songId: Long) {
        val currentlyLiked = _isLiked.value

        scope.launch {
            try {
                val result = if (currentlyLiked) {
                    deleteLikeSongUseCase(songId)
                } else {
                    addLikeSongUseCase(songId)
                }
                result.onSuccess {
                    _isLiked.value = !currentlyLiked
                    Log.d(TAG, "Like toggled: ${!currentlyLiked} (songId=$songId)")
                }
                result.onFailure { error ->
                    Log.e(TAG, "Error toggling like: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception toggling like: ${e.message}", e)
            }
        }
    }
}
