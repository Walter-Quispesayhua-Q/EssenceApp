package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.playback.domain.NowPlayingInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackError
import com.essence.essenceapp.feature.playback.domain.PlaybackPositionInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackQueue
import com.essence.essenceapp.feature.playback.domain.PlaybackRepeatMode
import com.essence.essenceapp.feature.playback.domain.PlaybackShuffleMode
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.domain.PlaybackUiState
import com.essence.essenceapp.feature.playback.engine.AudioPlayerEngine
import com.essence.essenceapp.feature.song.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Fachada publica del playback.
 *
 * Recibe acciones de la UI, service u otros modulos y delega el trabajo real a
 * piezas pequenas: cola, reproduccion, estado del engine y likes.
 */
@Singleton
class DefaultPlaybackController @Inject constructor(
    private val stateStore: PlaybackStateStore,
    private val queueCoordinator: PlaybackQueueCoordinator,
    private val songPlaybackCoordinator: PlaybackSongPlaybackCoordinator,
    private val engineStateHandler: PlaybackEngineStateHandler,
    private val likeHandler: PlaybackLikeHandler,
    private val errorRecoveryController: PlaybackErrorRecoveryController,
    private val audioPlayerEngine: AudioPlayerEngine,
    @ApplicationScope private val scope: CoroutineScope
) : PlaybackController {

    override val nowPlaying: StateFlow<NowPlayingInfo?> = stateStore.nowPlaying
    override val currentSong: StateFlow<Song?> = stateStore.currentSong
    override val queue: StateFlow<PlaybackQueue?> = stateStore.queue
    override val position: StateFlow<PlaybackPositionInfo> = stateStore.position
    override val playbackState: StateFlow<PlaybackState> = stateStore.playbackState
    override val repeatMode: StateFlow<PlaybackRepeatMode> = stateStore.repeatMode
    override val shuffleMode: StateFlow<PlaybackShuffleMode> = stateStore.shuffleMode
    override val errors: SharedFlow<PlaybackError> = stateStore.errors
    override val uiState: StateFlow<PlaybackUiState> = stateStore.uiState

    private var autoPlayOnRecovery = false

    init {
        audioPlayerEngine.state
            .onEach { engineState ->
                engineStateHandler.handle(
                    state = engineState,
                    onRefreshRequired = ::refreshCurrentSource,
                    onEnded = ::handlePlaybackEnded
                )
            }
            .launchIn(scope)
    }

    override fun dispatch(action: PlaybackAction) {
        when (action) {
            is PlaybackAction.Open -> open(action)
            PlaybackAction.Play -> play()
            PlaybackAction.Pause -> pause()
            PlaybackAction.TogglePlayPause -> togglePlayPause()
            PlaybackAction.Stop -> stop()

            is PlaybackAction.SeekTo -> audioPlayerEngine.seekTo(action.positionMs)
            is PlaybackAction.SeekBy -> seekBy(action.deltaMs)

            PlaybackAction.Next -> skipNext()
            PlaybackAction.Previous -> skipPrevious()
            is PlaybackAction.SkipTo -> skipTo(action.index)

            is PlaybackAction.SetRepeatMode -> setRepeatMode(action.mode)
            is PlaybackAction.SetShuffleMode -> stateStore.setShuffleMode(action.mode)
            PlaybackAction.ToggleRepeat -> toggleRepeat()

            is PlaybackAction.AddToQueueNext -> queueCoordinator.addNext(action.item)
            is PlaybackAction.AddToQueueEnd -> queueCoordinator.addEnd(action.item)
            is PlaybackAction.RemoveFromQueue -> removeFromQueue(action.index)
            is PlaybackAction.MoveInQueue -> moveInQueue(action.from, action.to)
            PlaybackAction.ClearUpcoming -> queueCoordinator.clearUpcoming()

            is PlaybackAction.ToggleLike -> likeHandler.toggleLike(action.songId)

            is PlaybackAction.SetCurrentLike -> likeHandler.setCurrentLike(
                songId = action.songId,
                isLiked = action.isLiked
            )
        }
    }

    private fun open(action: PlaybackAction.Open) {
        val request = action.request

        songPlaybackCoordinator.invalidate()
        engineStateHandler.resetEndedMarker()
        errorRecoveryController.reset()

        val opened = queueCoordinator.open(
            items = request.items,
            startIndex = request.startIndex
        )

        if (!opened) {
            stop()
            stateStore.tryFail(PlaybackError.QueueEmpty)
            return
        }

        if (request.autoPlay) {
            autoPlayOnRecovery = true
            startCurrent(
                startPositionMs = request.startPositionMs,
                forceRestart = true
            )
        } else {
            autoPlayOnRecovery = false
            stateStore.resetPosition(request.startPositionMs)
            stateStore.setPlaybackState(PlaybackState.Paused)
        }
    }

    private fun play() {
        autoPlayOnRecovery = true

        val currentItem = stateStore.currentItem

        if (currentItem == null) {
            stateStore.tryFail(PlaybackError.QueueEmpty)
            return
        }

        if (audioPlayerEngine.state.value.mediaId == currentItem.hlsMasterKey) {
            audioPlayerEngine.resume()
        } else {
            startCurrent(
                startPositionMs = stateStore.position.value.positionMs,
                forceRestart = true
            )
        }
    }

    private fun pause() {
        autoPlayOnRecovery = false
        audioPlayerEngine.pause()
    }

    private fun togglePlayPause() {
        if (stateStore.playbackState.value is PlaybackState.Playing) {
            pause()
        } else {
            play()
        }
    }

    private fun stop() {
        autoPlayOnRecovery = false
        songPlaybackCoordinator.invalidate()
        engineStateHandler.resetEndedMarker()
        errorRecoveryController.reset()

        queueCoordinator.stop()
        audioPlayerEngine.stop()
    }

    private fun seekBy(deltaMs: Long) {
        val targetPosition = (stateStore.position.value.positionMs + deltaMs)
            .coerceAtLeast(0L)

        audioPlayerEngine.seekTo(targetPosition)
    }

    private fun skipNext() {
        val currentQueue = stateStore.currentQueue ?: return

        val moved = queueCoordinator.next(stateStore.shuffleMode.value)

        if (moved) {
            autoPlayOnRecovery = true
            startCurrent(startPositionMs = 0L, forceRestart = true)
            return
        }

        if (
            stateStore.repeatMode.value == PlaybackRepeatMode.ALL &&
            currentQueue.items.isNotEmpty() &&
            queueCoordinator.rewindToFirst()
        ) {
            autoPlayOnRecovery = true
            startCurrent(startPositionMs = 0L, forceRestart = true)
        } else {
            endPlayback()
        }
    }

    private fun skipPrevious() {
        if (queueCoordinator.previous()) {
            autoPlayOnRecovery = true
            startCurrent(startPositionMs = 0L, forceRestart = true)
        }
    }

    private fun skipTo(index: Int) {
        if (queueCoordinator.skipTo(index)) {
            autoPlayOnRecovery = true
            startCurrent(startPositionMs = 0L, forceRestart = true)
        }
    }

    private fun removeFromQueue(index: Int) {
        when (queueCoordinator.removeAt(index)) {
            PlaybackQueueCoordinator.QueueMutationResult.CurrentChanged -> {
                autoPlayOnRecovery = true
                startCurrent(startPositionMs = 0L, forceRestart = true)
            }

            PlaybackQueueCoordinator.QueueMutationResult.Emptied -> {
                autoPlayOnRecovery = false
                audioPlayerEngine.stop()
            }

            PlaybackQueueCoordinator.QueueMutationResult.Changed,
            PlaybackQueueCoordinator.QueueMutationResult.Unchanged -> Unit
        }
    }

    private fun moveInQueue(
        from: Int,
        to: Int
    ) {
        queueCoordinator.move(from, to)
    }

    private fun setRepeatMode(mode: PlaybackRepeatMode) {
        stateStore.setRepeatMode(mode)
        audioPlayerEngine.setRepeatOne(mode == PlaybackRepeatMode.ONE)
    }

    private fun toggleRepeat() {
        val nextMode = when (stateStore.repeatMode.value) {
            PlaybackRepeatMode.OFF -> PlaybackRepeatMode.ONE
            PlaybackRepeatMode.ONE -> PlaybackRepeatMode.ALL
            PlaybackRepeatMode.ALL -> PlaybackRepeatMode.OFF
        }

        setRepeatMode(nextMode)
    }

    private fun startCurrent(
        startPositionMs: Long,
        forceRestart: Boolean
    ) {
        engineStateHandler.resetEndedMarker()

        songPlaybackCoordinator.playCurrent(
            startPositionMs = startPositionMs,
            forceRestart = forceRestart
        )

        queueCoordinator.prefetchUpcoming()
    }

    private fun refreshCurrentSource(): Boolean {
        val currentHlsMasterKey = stateStore.currentItem?.hlsMasterKey
        val shouldAutoPlay = autoPlayOnRecovery
        when (errorRecoveryController.tryStartSourceRefresh(currentHlsMasterKey)) {
            SourceRefreshStart.STARTED -> Unit
            SourceRefreshStart.ALREADY_RUNNING -> return true
            SourceRefreshStart.EXHAUSTED -> return false
        }

        audioPlayerEngine.clearSourceRefreshRequest()

        val job = songPlaybackCoordinator.playCurrent(
            startPositionMs = stateStore.position.value.positionMs,
            forceRestart = true,
            autoPlay = shouldAutoPlay
        )

        if (job == null) {
            errorRecoveryController.finishSourceRefresh()
            return false
        } else {
            job.invokeOnCompletion {
                errorRecoveryController.finishSourceRefresh()
            }
        }

        return true
    }

    private fun handlePlaybackEnded(mediaId: String?) {
        when {
            stateStore.repeatMode.value == PlaybackRepeatMode.ONE -> {
                autoPlayOnRecovery = true
                startCurrent(startPositionMs = 0L, forceRestart = true)
            }

            stateStore.currentQueue?.hasNext == true ||
                    stateStore.shuffleMode.value == PlaybackShuffleMode.ON -> {
                autoPlayOnRecovery = true
                skipNext()
            }

            stateStore.repeatMode.value == PlaybackRepeatMode.ALL -> {
                if (queueCoordinator.rewindToFirst()) {
                    autoPlayOnRecovery = true
                    startCurrent(startPositionMs = 0L, forceRestart = true)
                } else {
                    endPlayback()
                }
            }

            else -> {
                endPlayback()
            }
        }
    }

    private fun endPlayback() {
        autoPlayOnRecovery = false
        queueCoordinator.cancelPrefetch()
        stateStore.setPlaybackState(PlaybackState.Ended)
    }
}
