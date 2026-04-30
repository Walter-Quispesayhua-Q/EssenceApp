package com.essence.essenceapp.feature.playlist.ui.addsongs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playlist.domain.usecase.GetListSongsUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistUseCase
import com.essence.essenceapp.feature.playlist.ui.addsongs.domain.EnsureAndAddSongToPlaylistInteractor
import com.essence.essenceapp.feature.search.domain.model.Search
import com.essence.essenceapp.feature.search.domain.usecase.SearchUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class PlaylistAddSongsViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val ensureAndAddSongToPlaylistInteractor: EnsureAndAddSongToPlaylistInteractor,
    private val getListSongsUseCase: GetListSongsUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistAddSongsUiState>(PlaylistAddSongsUiState.Idle)
    val uiState: StateFlow<PlaylistAddSongsUiState> = _uiState.asStateFlow()

    private val _playlistTitle = MutableStateFlow<String?>(null)
    val playlistTitle: StateFlow<String?> = _playlistTitle.asStateFlow()

    private val _addedDuringSession = MutableStateFlow(0)
    val addedDuringSession: StateFlow<Int> = _addedDuringSession.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    private var playlistId: Long = 0L
    private var currentQuery: String = ""
    private var currentPage: Int = 0
    private var currentAddedSongKeys: Set<String> = emptySet()

    private var existingSongsJob: Job? = null

    init {
        viewModelScope.launch {
            queryFlow
                .drop(1)
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { search() }
        }
    }

    fun initialize(playlistId: Long) {
        if (this.playlistId == playlistId && existingSongsJob != null) return
        this.playlistId = playlistId
        loadExistingSongs()
        loadPlaylistMetadata()
    }

    private fun loadPlaylistMetadata() {
        if (playlistId <= 0L) return
        viewModelScope.launch {
            try {
                val result = getPlaylistUseCase(playlistId)
                result.onSuccess { playlist ->
                    _playlistTitle.value = playlist.title
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }
    }

    fun onAction(action: PlaylistAddSongsAction) {
        when (action) {
            is PlaylistAddSongsAction.QueryChanged -> {
                currentQuery = action.value
                queryFlow.value = action.value
                val current = _uiState.value
                if (current is PlaylistAddSongsUiState.Success) {
                    _uiState.value = current.copy(query = action.value)
                }
            }

            PlaylistAddSongsAction.Submit -> search()
            is PlaylistAddSongsAction.AddSong -> addSong(action.songKey)
            PlaylistAddSongsAction.LoadNextPage -> loadNextPage()
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L
    }

    private fun loadExistingSongs() {
        if (playlistId <= 0L) return

        existingSongsJob = viewModelScope.launch {
            val result = try {
                getListSongsUseCase(playlistId)
            } catch (e: CancellationException) {
                throw e
            } catch (error: Exception) {
                Result.failure(error)
            }

            result.onSuccess { songs ->
                currentAddedSongKeys = songs.map { it.hlsMasterKey }.toSet()
                val current = _uiState.value as? PlaylistAddSongsUiState.Success ?: return@onSuccess
                _uiState.value = current.copy(
                    addedSongKeys = current.addedSongKeys + currentAddedSongKeys
                )
            }
        }
    }

    private fun search() {
        if (currentQuery.isBlank()) {
            _uiState.value = PlaylistAddSongsUiState.Idle
            currentPage = 0
            return
        }

        viewModelScope.launch {
            _uiState.value = PlaylistAddSongsUiState.Loading
            currentPage = 0

            existingSongsJob?.join()

            val result = try {
                searchSongsPage(page = 0)
            } catch (e: CancellationException) {
                throw e
            } catch (error: Exception) {
                Result.failure(error)
            }

            result.onSuccess { searchResult ->
                _uiState.value = PlaylistAddSongsUiState.Success(
                    query = currentQuery,
                    songs = searchResult.songs.orEmpty(),
                    addedSongKeys = currentAddedSongKeys,
                    hasNextPage = searchResult.hasNextPage
                )
            }

            result.onFailure { error ->
                _uiState.value = PlaylistAddSongsUiState.Error(error.toUserMessage())
            }
        }
    }

    private fun loadNextPage() {
        val current = _uiState.value as? PlaylistAddSongsUiState.Success ?: return
        if (current.isLoadingNextPage || !current.hasNextPage || currentQuery.isBlank()) return

        val nextPage = currentPage + 1
        _uiState.value = current.copy(isLoadingNextPage = true)

        viewModelScope.launch {
            val result = try {
                searchSongsPage(page = nextPage)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                null
            }

            val latest = _uiState.value as? PlaylistAddSongsUiState.Success ?: return@launch
            if (result == null) {
                _uiState.value = latest.copy(isLoadingNextPage = false)
                return@launch
            }

            result.onSuccess { searchResult ->
                currentPage = nextPage
                _uiState.value = latest.copy(
                    songs = (latest.songs + searchResult.songs.orEmpty()).distinctBy { it.hlsMasterKey },
                    addedSongKeys = currentAddedSongKeys,
                    hasNextPage = searchResult.hasNextPage,
                    isLoadingNextPage = false
                )
            }

            result.onFailure {
                _uiState.value = latest.copy(isLoadingNextPage = false)
            }
        }
    }

    private suspend fun searchSongsPage(page: Int): Result<Search> {
        val attempts = listOf("song", "songs")
        var emptyResult: Search? = null
        var lastError: Throwable? = null

        for (type in attempts) {
            val result = searchUseCase(
                query = currentQuery,
                type = type,
                page = page
            )

            if (result.isSuccess) {
                val search = result.getOrNull() ?: continue
                if (search.songs.orEmpty().isNotEmpty() || search.hasNextPage || type == attempts.last()) {
                    return Result.success(search)
                }
                emptyResult = search
            } else if (lastError == null) {
                lastError = result.exceptionOrNull()
            }
        }

        return emptyResult?.let { Result.success(it) }
            ?: Result.failure(lastError ?: Exception("No se pudo buscar canciones"))
    }

    private fun addSong(songKey: String) {
        val current = _uiState.value as? PlaylistAddSongsUiState.Success ?: return
        if (songKey in current.addingSongKeys || songKey in current.addedSongKeys) return

        _uiState.value = current.copy(addingSongKeys = current.addingSongKeys + songKey)

        viewModelScope.launch {
            val result = try {
                ensureAndAddSongToPlaylistInteractor(playlistId, songKey)
            } catch (e: CancellationException) {
                throw e
            } catch (error: Exception) {
                Result.failure(error)
            }

            val latest = _uiState.value as? PlaylistAddSongsUiState.Success ?: return@launch

            result.onSuccess {
                currentAddedSongKeys = currentAddedSongKeys + songKey
                _addedDuringSession.value = _addedDuringSession.value + 1
                _uiState.value = latest.copy(
                    addedSongKeys = latest.addedSongKeys + songKey,
                    addingSongKeys = latest.addingSongKeys - songKey
                )
            }

            result.onFailure {
                _uiState.value = latest.copy(
                    addingSongKeys = latest.addingSongKeys - songKey
                )
            }
        }
    }
}
