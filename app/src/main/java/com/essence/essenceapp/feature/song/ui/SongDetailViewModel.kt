package com.essence.essenceapp.feature.song.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playback.domain.NowPlayingInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.domain.PlaybackSource
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.domain.PlaybackUiState
import com.essence.essenceapp.feature.playback.mapper.toQueueItem
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.shared.cache.QueueCache
import com.essence.essenceapp.shared.cache.SongDetailCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val playbackController: PlaybackController,
    private val getSongUseCase: GetSongUseCase,
    private val addLikeSongUseCase: AddLikeSongUseCase,
    private val deleteLikeSongUseCase: DeleteLikeSongUseCase,
    private val queueCache: QueueCache,
    private val songDetailCache: SongDetailCache
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongDetailUiState>(SongDetailUiState.Loading)
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()

    private var currentHlsMasterKey: String? = null

    init {
        observePlaybackState()
        observeQueue()
        observeNowPlaying()
        observeCurrentSong()
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            playbackController.uiState.collect { playback ->
                val failedMessage = (playback.playbackState as? PlaybackState.Failed)
                    ?.cause
                    ?.message

                if (failedMessage != null) {
                    val current = _uiState.value
                    if (current is SongDetailUiState.Loading ||
                        current is SongDetailUiState.LoadingNextSong
                    ) {
                        _uiState.value = SongDetailUiState.Error(failedMessage)
                        return@collect
                    }
                }

                when (val current = _uiState.value) {
                    is SongDetailUiState.Success ->
                        _uiState.value = current.copy(playback = playback)
                    is SongDetailUiState.LoadingNextSong ->
                        _uiState.value = current.copy(playback = playback)
                    else -> Unit
                }
            }
        }
    }

    private fun observeQueue() {
        viewModelScope.launch {
            playbackController.queue.collect { queue ->
                val items = queue?.items ?: emptyList()
                val index = queue?.currentIndex ?: -1
                when (val current = _uiState.value) {
                    is SongDetailUiState.Success ->
                        _uiState.value = current.copy(
                            queueItems = items,
                            queueCurrentIndex = index
                        )
                    is SongDetailUiState.LoadingNextSong ->
                        _uiState.value = current.copy(
                            queueItems = items,
                            queueCurrentIndex = index
                        )
                    else -> Unit
                }
            }
        }
    }

    private fun observeNowPlaying() {
        viewModelScope.launch {
            playbackController.nowPlaying.drop(1).collect { info ->
                val lookup = info?.item?.hlsMasterKey ?: return@collect
                if (lookup == currentHlsMasterKey) return@collect

                currentHlsMasterKey = lookup
                if (showResolvedSongIfAvailable(lookup)) return@collect

                showLoadingForNextSong(info)
            }
        }
    }

    private fun observeCurrentSong() {
        viewModelScope.launch {
            playbackController.currentSong.collect { song ->
                if (song == null) return@collect
                if (song.hlsMasterKey != currentHlsMasterKey) return@collect

                songDetailCache.put(song.hlsMasterKey, song)
                when (val current = _uiState.value) {
                    is SongDetailUiState.Success -> {
                        if (current.song != song) {
                            _uiState.value = current.copy(song = song)
                        }
                    }
                    else -> showSuccess(song)
                }
            }
        }
    }

    fun loadSong(lookup: String) {
        currentHlsMasterKey = lookup

        val cached = songDetailCache.get(lookup)
        if (cached != null) {
            showSuccess(cached)
            openSingleIfNeeded(cached)
            return
        }

        val resolved = playbackController.currentSong.value
        if (resolved != null && resolved.hlsMasterKey == lookup) {
            songDetailCache.put(lookup, resolved)
            showSuccess(resolved)
            return
        }

        showPreviewFor(lookup)

        if (isCurrentPlaybackLookup(lookup)) {
            return
        }

        viewModelScope.launch { fetchSongDetail(lookup) }
    }

    fun onAction(action: SongDetailAction) {
        when (action) {
            SongDetailAction.Back -> Unit
            SongDetailAction.Refresh -> currentHlsMasterKey?.let { lookup ->
                showPreviewFor(lookup)

                if (isCurrentPlaybackLookup(lookup)) {
                    playbackController.dispatch(PlaybackAction.Play)
                } else {
                    viewModelScope.launch { fetchSongDetail(lookup) }
                }
            }
            is SongDetailAction.OpenAlbum -> Unit
            is SongDetailAction.OpenArtist -> Unit
            SongDetailAction.AddToPlaylist -> Unit
            SongDetailAction.ToggleLike -> toggleLike()
            is SongDetailAction.PlayQueueItem ->
                playbackController.dispatch(PlaybackAction.SkipTo(action.index))
        }
    }

    fun onPlaybackAction(action: PlaybackAction) {
        playbackController.dispatch(action)
    }

    private suspend fun fetchSongDetail(lookup: String) {
        val result = getSongUseCase(lookup)
        if (lookup != currentHlsMasterKey) return

        result.onSuccess { song ->
            songDetailCache.put(lookup, song)
            showSuccess(song)
            openSingleIfNeeded(song)
        }
        result.onFailure { error ->
            val current = _uiState.value
            if (current !is SongDetailUiState.Success) {
                _uiState.value = SongDetailUiState.Error(
                    error.message ?: "No se pudo cargar la canción."
                )
            }
        }
    }

    private fun toggleLike() {
        val current = _uiState.value as? SongDetailUiState.Success ?: return
        if (current.isLikeSubmitting) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLikeSubmitting = true)

            val result = try {
                if (current.song.isLiked) {
                    deleteLikeSongUseCase(current.song.id)
                } else {
                    addLikeSongUseCase(current.song.id)
                }
            } catch (error: Exception) {
                Result.failure(error)
            }

            result.onSuccess {
                val latest = _uiState.value as? SongDetailUiState.Success ?: return@onSuccess
                val updatedSong = latest.song.copy(isLiked = !current.song.isLiked)
                currentHlsMasterKey?.let { songDetailCache.put(it, updatedSong) }
                _uiState.value = latest.copy(
                    song = updatedSong,
                    isLikeSubmitting = false
                )

                playbackController.dispatch(
                    PlaybackAction.SetCurrentLike(
                        songId = updatedSong.id,
                        isLiked = updatedSong.isLiked
                    )
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }

    private fun findPreviewItem(lookup: String): PlaybackQueueItem? {
        val queue = playbackController.queue.value
        return queue?.items?.firstOrNull { it.hlsMasterKey == lookup }
            ?: queueCache.findItem(lookup)?.toQueueItem()
    }

    private fun showPreviewFor(
        lookup: String,
        previewItem: PlaybackQueueItem? = findPreviewItem(lookup)
    ) {
        val queue = playbackController.queue.value
        if (previewItem != null) {
            _uiState.value = SongDetailUiState.LoadingNextSong(
                title = previewItem.title,
                artistName = previewItem.artistName,
                imageKey = previewItem.imageKey,
                durationMs = previewItem.durationMs,
                playback = playbackController.uiState.value,
                queueItems = queue?.items ?: emptyList(),
                queueCurrentIndex = queue?.currentIndex ?: -1
            )
        } else {
            _uiState.value = SongDetailUiState.Loading
        }
    }

    private fun showSuccess(song: Song) {
        val queue = playbackController.queue.value
        _uiState.value = SongDetailUiState.Success(
            song = song,
            playback = playbackController.uiState.value,
            isLikeSubmitting = false,
            queueItems = queue?.items ?: emptyList(),
            queueCurrentIndex = queue?.currentIndex ?: -1
        )
    }

    private fun showResolvedSongIfAvailable(lookup: String): Boolean {
        val resolved = playbackController.currentSong.value
        if (resolved != null && resolved.hlsMasterKey == lookup) {
            songDetailCache.put(lookup, resolved)
            showSuccess(resolved)
            return true
        }

        val current = _uiState.value as? SongDetailUiState.Success
        if (current?.song?.hlsMasterKey == lookup) return true

        val cached = songDetailCache.get(lookup)
        if (cached != null) {
            showSuccess(cached)
            return true
        }

        return false
    }

    private fun showLoadingForNextSong(info: NowPlayingInfo) {
        val queue = playbackController.queue.value
        _uiState.value = SongDetailUiState.LoadingNextSong(
            title = info.item.title,
            artistName = info.item.artistName,
            imageKey = info.item.imageKey,
            durationMs = info.item.durationMs,
            playback = playbackController.uiState.value,
            queueItems = queue?.items ?: emptyList(),
            queueCurrentIndex = queue?.currentIndex ?: -1
        )
    }

    private fun isCurrentPlaybackLookup(lookup: String): Boolean {
        val nowPlayingItem = playbackController.nowPlaying.value?.item
        val queueCurrentItem = playbackController.queue.value?.current
        val resolvedSong = playbackController.currentSong.value

        return nowPlayingItem?.hlsMasterKey == lookup ||
            queueCurrentItem?.hlsMasterKey == lookup ||
            resolvedSong?.hlsMasterKey == lookup
    }

    private fun openSingleIfNeeded(song: Song) {
        val queue = playbackController.queue.value
        val alreadyInPlayback = queue?.items?.any {
            it.hlsMasterKey == song.hlsMasterKey
        } == true
        if (alreadyInPlayback) return

        playbackController.dispatch(
            PlaybackAction.Open(
                PlaybackOpenRequest(
                    items = listOf(song.toQueueItem()),
                    startIndex = 0,
                    source = PlaybackSource(PlaybackSource.SourceType.SINGLE)
                )
            )
        )
    }
}
