package com.essence.essenceapp.shared.ui.components.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.usecase.GetListSongsUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistsByUserUseCase
import com.essence.essenceapp.feature.playlist.ui.addsongs.domain.EnsureAndAddSongToPlaylistInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaylistEntry(
    val playlist: PlaylistSimple,
    val alreadyContainsSong: Boolean
)

data class AddToPlaylistState(
    val playlists: List<PlaylistEntry> = emptyList(),
    val isLoading: Boolean = true,
    val addedToPlaylistId: Long? = null,
    val addingToPlaylistId: Long? = null,
    val error: String? = null
)

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    private val getPlaylistsByUserUseCase: GetPlaylistsByUserUseCase,
    private val getListSongsUseCase: GetListSongsUseCase,
    private val ensureAndAddSongToPlaylistInteractor: EnsureAndAddSongToPlaylistInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(AddToPlaylistState())
    val state: StateFlow<AddToPlaylistState> = _state.asStateFlow()

    fun prepare(songKey: String) {
        loadPlaylistsAndMembership(songKey)
    }

    private fun loadPlaylistsAndMembership(songKey: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                addedToPlaylistId = null,
                addingToPlaylistId = null,
                error = null
            )

            try {
                val result = getPlaylistsByUserUseCase()
                result.onSuccess { data ->
                    val playlists = data.myPlaylists.orEmpty()
                    val entries = coroutineScope {
                        playlists.map { playlist ->
                            async {
                                PlaylistEntry(
                                    playlist = playlist,
                                    alreadyContainsSong = playlistContainsSong(playlist.id, songKey)
                                )
                            }
                        }.awaitAll()
                    }
                    _state.value = _state.value.copy(
                        playlists = entries,
                        isLoading = false,
                        error = null
                    )
                }
                result.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar playlists"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error inesperado"
                )
            }
        }
    }

    private suspend fun playlistContainsSong(playlistId: Long, songKey: String): Boolean {
        return try {
            val result = getListSongsUseCase(playlistId)
            result.getOrNull()?.any { it.hlsMasterKey == songKey } ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun addSongToPlaylist(playlistId: Long, songKey: String) {
        if (_state.value.addingToPlaylistId != null) return

        val entry = _state.value.playlists.firstOrNull { it.playlist.id == playlistId }
        if (entry?.alreadyContainsSong == true) return

        _state.value = _state.value.copy(
            addingToPlaylistId = playlistId,
            addedToPlaylistId = null,
            error = null
        )

        viewModelScope.launch {
            try {
                val result = ensureAndAddSongToPlaylistInteractor(playlistId, songKey)
                result.onSuccess {
                    _state.value = _state.value.copy(
                        addedToPlaylistId = playlistId,
                        addingToPlaylistId = null,
                        playlists = _state.value.playlists.map { item ->
                            if (item.playlist.id == playlistId) item.copy(alreadyContainsSong = true)
                            else item
                        }
                    )
                }
                result.onFailure {
                    _state.value = _state.value.copy(
                        addingToPlaylistId = null,
                        error = "No se pudo agregar la cancion"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    addingToPlaylistId = null,
                    error = "Error inesperado"
                )
            }
        }
    }
}