package com.essence.essenceapp.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.storage.TokenManager
import com.essence.essenceapp.feature.history.domain.usecase.GetSongsOfHistoryUseCase
import com.essence.essenceapp.feature.home.domain.usecase.ObserveHomeUseCase
import com.essence.essenceapp.feature.home.domain.usecase.RefreshHomeUseCase
import com.essence.essenceapp.feature.profile.domain.usecase.GetCurrentUserUseCase
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackHistoryRecorder
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val HOME_HISTORY_LIMIT = 10
private const val HOME_REFRESH_THRESHOLD_MS = 30_000L

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeHomeUseCase: ObserveHomeUseCase,
    private val refreshHomeUseCase: RefreshHomeUseCase,
    private val getSongsOfHistoryUseCase: GetSongsOfHistoryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val tokenManager: TokenManager,
    private val playbackHistoryRecorder: PlaybackHistoryRecorder
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)
    private val recentSongs = MutableStateFlow<List<SongSimple>>(emptyList())
    private var lastSuccessfulRefreshAt: Long = 0L

    val uiState: StateFlow<HomeUiState> = combine(
        observeHomeUseCase(),
        refreshing,
        refreshError,
        recentSongs
    ) { home, isRefreshing, error, recent ->
        when {
            home != null -> HomeUiState.Success(
                homeData = home,
                recentSongs = recent,
                isRefreshing = isRefreshing
            )
            isRefreshing -> HomeUiState.Loading
            error != null -> HomeUiState.Error(error)
            else -> HomeUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = computeInitialState()
    )

    private fun computeInitialState(): HomeUiState {
        val cached = observeHomeUseCase().value
        return if (cached != null) {
            HomeUiState.Success(
                homeData = cached,
                recentSongs = recentSongs.value,
                isRefreshing = false
            )
        } else {
            HomeUiState.Loading
        }
    }

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    init {
        loadCurrentUser()
        refreshHistory()
        refreshIfStale()
        observeHistoryEvents()
    }

    fun refreshIfStale() {
        val hasCache = observeHomeUseCase().value != null
        val isFresh = System.currentTimeMillis() - lastSuccessfulRefreshAt < HOME_REFRESH_THRESHOLD_MS
        if (hasCache && isFresh) return
        refresh()
    }

    private fun observeHistoryEvents() {
        playbackHistoryRecorder.historyRecorded
            .onEach { refreshHistory() }
            .launchIn(viewModelScope)
    }

    fun onRefresh() {
        loadCurrentUser()
        refreshHistory()
        refresh()
    }

    fun refreshHistory() {
        viewModelScope.launch {
            runCatching { getSongsOfHistoryUseCase(limit = HOME_HISTORY_LIMIT) }
                .getOrNull()
                ?.onSuccess { songs -> recentSongs.value = songs }
        }
    }

    fun refreshCurrentUser() {
        loadCurrentUser()
    }

    fun clearCurrentUser() {
        _username.value = null
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            refreshError.value = null

            refreshHomeUseCase()
                .onSuccess { lastSuccessfulRefreshAt = System.currentTimeMillis() }
                .onFailure { error -> refreshError.value = error.toUserMessage() }

            refreshing.value = false
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val token = runCatching { tokenManager.token.first() }.getOrNull()
            if (token.isNullOrBlank()) {
                _username.value = null
                return@launch
            }

            runCatching { getCurrentUserUseCase() }
                .onSuccess { result ->
                    result
                        .onSuccess { currentUser -> _username.value = currentUser.username }
                        .onFailure { _username.value = null }
                }
                .onFailure { _username.value = null }
        }
    }
}