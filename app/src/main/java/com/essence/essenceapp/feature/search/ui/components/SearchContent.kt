package com.essence.essenceapp.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.ui.SearchAction
import com.essence.essenceapp.feature.search.ui.SearchUiState
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.album.CompactAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CompactArtistContent
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    onAction: (SearchAction) -> Unit
) {
    when (state) {
        is SearchUiState.Idle -> SearchIdleContent(modifier = modifier)
        is SearchUiState.Editing -> SearchEditingContent(
            modifier = modifier,
            state = state,
            onAction = onAction
        )
        is SearchUiState.Success -> SearchSuccessContent(
            modifier = modifier,
            state = state
        )
    }
}

@Composable
private fun SearchIdleContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BaseCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Búsqueda",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Escribe algo en la barra superior para comenzar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun SearchEditingContent(
    modifier: Modifier = Modifier,
    state: SearchUiState.Editing,
    onAction: (SearchAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchFiltersCard(
                state = state,
                onAction = onAction
            )
        }

        if (state.isSubmitting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun SearchFiltersCard(
    state: SearchUiState.Editing,
    onAction: (SearchAction) -> Unit
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Filtrar búsqueda",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Selecciona una categoría para afinar resultados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            CategorySection(
                isLoading = state.isCategoriesLoading,
                error = state.categoriesError,
                categories = state.categories,
                selectedType = state.form.type,
                onCategoryClick = { selected ->
                    onAction(SearchAction.TypeChanged(selected))
                    if (state.errorMessage != null) onAction(SearchAction.ClearError)
                }
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CategorySection(
    isLoading: Boolean,
    error: String?,
    categories: List<Category>,
    selectedType: String,
    onCategoryClick: (String) -> Unit
) {
    when {
        isLoading -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Cargando categorías...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }

        error != null -> {
            Text(
                text = "Error: $error",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        categories.isEmpty() -> {
            Text(
                text = "No hay categorías",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }

        else -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories, key = { it.value }) { category ->
                    FilterChip(
                        selected = selectedType == category.value,
                        onClick = { onCategoryClick(category.value) },
                        label = { Text(category.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSuccessContent(
    modifier: Modifier = Modifier,
    state: SearchUiState.Success
) {
    val selectedType = state.form.type.lowercase()
    val songs = state.results.songs.orEmpty()
    val albums = state.results.albums.orEmpty()
    val artists = state.results.artists.orEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Resultados",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Query: ${state.form.query}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
            }
        }

        if (showSongs(selectedType) && songs.isNotEmpty()) {
            item { SectionTitle("Canciones") }
            items(songs.take(10), key = { it.id }) { song ->
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    CompactSongContent(song = song)
                }
            }
        }

        if (showAlbums(selectedType) && albums.isNotEmpty()) {
            item { SectionTitle("Álbumes") }
            items(albums.take(10), key = { it.id }) { album ->
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    CompactAlbumContent(album = album)
                }
            }
        }

        if (showArtists(selectedType) && artists.isNotEmpty()) {
            item { SectionTitle("Artistas") }
            items(artists.take(10), key = { it.id }) { artist ->
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    CompactArtistContent(artist = artist)
                }
            }
        }

        if (songs.isEmpty() && albums.isEmpty() && artists.isEmpty()) {
            item {
                BaseCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = "No se encontraron resultados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 6.dp)
    )
}

private fun showSongs(type: String): Boolean =
    type.isBlank() || type == "song"
private fun showAlbums(type: String): Boolean =
    type.isBlank() || type == "album"
private fun showArtists(type: String): Boolean =
    type.isBlank() || type == "artist"

@Preview(name = "Search - Idle", showBackground = true)
@Composable
private fun SearchIdlePreview() {
    EssenceAppTheme {
        SearchContent(
            state = SearchUiState.Idle,
            onAction = {}
        )
    }
}

@Preview(name = "Search - Editing / Loading categories", showBackground = true)
@Composable
private fun SearchEditingLoadingCategoriesPreview() {
    EssenceAppTheme {
        SearchContent(
            state = SearchUiState.Editing(
                isCategoriesLoading = true
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Search - Editing / Categories ready", showBackground = true)
@Composable
private fun SearchEditingReadyPreview() {
    EssenceAppTheme {
        SearchContent(
            state = SearchUiState.Editing(
                isCategoriesLoading = false,
                categories = listOf(
                    Category(label = "Todo", value = ""),
                    Category(label = "Canciones", value = "song"),
                    Category(label = "Álbumes", value = "album"),
                    Category(label = "Artistas", value = "artist")
                )
            ),
            onAction = {}
        )
    }
}