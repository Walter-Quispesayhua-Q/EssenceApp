package com.essence.essenceapp.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.search.domain.SearchType
import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.model.Search
import com.essence.essenceapp.feature.search.domain.normalizeSearchType
import com.essence.essenceapp.feature.search.domain.usecase.GetAvailableCategoriesUseCase
import com.essence.essenceapp.feature.search.domain.usecase.SearchUseCase
import com.essence.essenceapp.shared.cache.QueueCache
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val getAvailableCategoriesUseCase: GetAvailableCategoriesUseCase,
    private val queueCache: QueueCache
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Editing())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val typeFlow = MutableStateFlow("")

    private var cachedCategories: List<Category> = emptyList()
    private var nextPageJob: Job? = null

    init {
        loadCategories()
        observeSearchInput()
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.QueryChanged -> {
                updateEditing { it.copy(form = it.form.copy(query = action.value)) }
                queryFlow.value = action.value
            }

            is SearchAction.TypeChanged -> {
                updateEditing { it.copy(form = it.form.copy(type = action.value)) }
                typeFlow.value = action.value
            }

            SearchAction.Submit -> {
                queryFlow.value = currentForm().query
                typeFlow.value = currentForm().type
            }

            SearchAction.ClearError -> updateEditing { it.copy(errorMessage = null) }

            SearchAction.LoadNextPage -> loadNextPage()
        }
    }

    private fun observeSearchInput() {
        combine(
            queryFlow.debounce(DEBOUNCE_MS),
            typeFlow
        ) { query, type -> query.trim() to type.trim() }
            .distinctUntilChanged()
            .onEach { (query, type) ->
                if (query.length < SearchFormState.MIN_QUERY_LENGTH) {
                    nextPageJob?.cancel()
                    revertToEditing()
                }
            }
            .flatMapLatest { (query, type) ->
                if (query.length < SearchFormState.MIN_QUERY_LENGTH) {
                    flow { }
                } else {
                    flow {
                        markSubmitting()
                        emit(searchUseCase(query, type.takeIf { it.isNotBlank() }, 0) to (query to type))
                    }
                }
            }
            .onEach { (result, queryAndType) ->
                val (query, type) = queryAndType
                result.onSuccess { searchResult ->
                    cacheSongsForQueue(searchResult.songs.orEmpty())
                    _uiState.value = SearchUiState.Success(
                        form = SearchFormState(query = query, type = type),
                        results = searchResult,
                        page = 0,
                        isLoadingNextPage = false
                    )
                }
                result.onFailure { error ->
                    updateEditing {
                        it.copy(isSubmitting = false, errorMessage = error.toUserMessage())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            updateEditing { it.copy(isCategoriesLoading = true, categoriesError = null) }

            val result = runCatching { getAvailableCategoriesUseCase() }
                .getOrElse { Result.failure(it) }

            result.onSuccess { categories ->
                cachedCategories = categories
                updateEditing {
                    it.copy(
                        categories = categories,
                        isCategoriesLoading = false,
                        categoriesError = null
                    )
                }
            }
            result.onFailure { error ->
                updateEditing {
                    it.copy(
                        isCategoriesLoading = false,
                        categoriesError = error.toUserMessage()
                    )
                }
            }
        }
    }

    private fun loadNextPage() {
        val current = _uiState.value as? SearchUiState.Success ?: return
        if (current.isLoadingNextPage) return
        if (!current.results.hasNextPage) return
        if (nextPageJob?.isActive == true) return

        val nextPage = current.page + 1
        _uiState.value = current.copy(isLoadingNextPage = true)

        nextPageJob = viewModelScope.launch {
            val result = runCatching {
                searchUseCase(
                    query = current.form.trimmedQuery,
                    type = current.form.type.takeIf { it.isNotBlank() },
                    page = nextPage
                )
            }.getOrElse { Result.failure(it) }

            val latest = _uiState.value as? SearchUiState.Success ?: return@launch
            if (latest.form != current.form) return@launch

            result.onSuccess { nextResults ->
                val merged = mergeResults(
                    current = latest.results,
                    next = nextResults,
                    selectedType = latest.form.type
                )
                cacheSongsForQueue(merged.songs.orEmpty())
                _uiState.value = latest.copy(
                    results = merged,
                    page = nextPage,
                    isLoadingNextPage = false
                )
            }
            result.onFailure {
                _uiState.value = latest.copy(isLoadingNextPage = false)
            }
        }
    }

    private fun mergeResults(current: Search, next: Search, selectedType: String): Search {
        val mergedSongs = (current.songs.orEmpty() + next.songs.orEmpty())
            .distinctBy { it.detailLookup }
        val mergedAlbums = (current.albums.orEmpty() + next.albums.orEmpty())
            .distinctBy { it.detailLookup }
        val mergedArtists = (current.artists.orEmpty() + next.artists.orEmpty())
            .distinctBy { it.detailLookup }

        return when (normalizeSearchType(selectedType)) {
            SearchType.SONG -> current.copy(songs = mergedSongs, hasNextPage = next.hasNextPage)
            SearchType.ALBUM -> current.copy(albums = mergedAlbums, hasNextPage = next.hasNextPage)
            SearchType.ARTIST -> current.copy(artists = mergedArtists, hasNextPage = next.hasNextPage)
            else -> current.copy(
                songs = mergedSongs,
                albums = mergedAlbums,
                artists = mergedArtists,
                hasNextPage = next.hasNextPage
            )
        }
    }

    private fun cacheSongsForQueue(songs: List<com.essence.essenceapp.feature.song.domain.model.SongSimple>) {
        if (songs.isNotEmpty()) {
            queueCache.set(SOURCE_KEY, songs)
        }
    }

    private fun markSubmitting() {
        val current = _uiState.value
        if (current is SearchUiState.Editing) {
            _uiState.value = current.copy(isSubmitting = true, errorMessage = null)
        }
    }

    private fun revertToEditing() {
        val current = _uiState.value
        if (current is SearchUiState.Success) {
            _uiState.value = SearchUiState.Editing(
                form = current.form,
                categories = cachedCategories,
                isCategoriesLoading = false
            )
        } else if (current is SearchUiState.Editing && current.isSubmitting) {
            _uiState.value = current.copy(isSubmitting = false)
        }
    }

    private fun currentForm(): SearchFormState = when (val current = _uiState.value) {
        is SearchUiState.Editing -> current.form
        is SearchUiState.Success -> current.form
        is SearchUiState.Idle -> SearchFormState()
    }

    private fun updateEditing(transform: (SearchUiState.Editing) -> SearchUiState.Editing) {
        val editing = when (val current = _uiState.value) {
            is SearchUiState.Editing -> current
            is SearchUiState.Success -> SearchUiState.Editing(
                form = current.form,
                categories = cachedCategories,
                isCategoriesLoading = false
            )
            is SearchUiState.Idle -> SearchUiState.Editing(
                categories = cachedCategories,
                isCategoriesLoading = false
            )
        }
        _uiState.value = transform(editing)
    }

    companion object {
        private const val DEBOUNCE_MS = 350L
        private const val SOURCE_KEY = "search"
    }
}