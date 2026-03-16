package com.essence.essenceapp.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.ui.SearchAction
import com.essence.essenceapp.feature.search.ui.SearchUiState
import com.essence.essenceapp.shared.ui.components.cards.album.CompactAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CompactArtistContent
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.SoftRose
import androidx.compose.ui.graphics.Brush

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    onAction: (SearchAction) -> Unit,
    onOpenSong: (String) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {},
    onOpenArtist: (String) -> Unit = {}
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
            state = state,
            onOpenSong = onOpenSong,
            onOpenAlbum = onOpenAlbum,
            onOpenArtist = onOpenArtist
        )
    }
}

@Composable
private fun SearchIdleContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Descubre música",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Busca canciones, artistas o álbumes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchEditingContent(
    modifier: Modifier = Modifier,
    state: SearchUiState.Editing,
    onAction: (SearchAction) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchFiltersIsland(state = state, onAction = onAction)
        }

        if (state.isSubmitting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
private fun SearchFiltersIsland(
    state: SearchUiState.Editing,
    onAction: (SearchAction) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filtrar búsqueda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Selecciona una categoría para afinar resultados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Cargando categorías...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        error != null -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        categories.isEmpty() -> {
            Text(
                text = "No hay categorías disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        else -> {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.value }) { category ->
                    val isSelected = selectedType == category.value
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryClick(category.value) },
                        label = {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SoftRose.copy(alpha = 0.15f),
                            selectedLabelColor = SoftRose,
                            containerColor = GraphiteSurface,
                            labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSuccessContent(
    modifier: Modifier = Modifier,
    state: SearchUiState.Success,
    onOpenSong: (String) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit
) {
    val selectedType = state.form.type.lowercase()
    val songs = state.results.songs.orEmpty()
    val albums = state.results.albums.orEmpty()
    val artists = state.results.artists.orEmpty()
    val bottomClearance = LocalBottomBarClearance.current
    val hasResults = songs.isNotEmpty() || albums.isNotEmpty() || artists.isNotEmpty()

    if (!hasResults) {
        EmptyResultsContent(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = bottomClearance + 16.dp
        )
    ) {
        if (showSongs(selectedType) && songs.isNotEmpty()) {
            item {
                SearchSectionTitle(
                    title = "Canciones",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                ResultsIsland(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val displaySongs = songs.take(10)
                    displaySongs.forEachIndexed { index, song ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenSong(song.detailLookup) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            CompactSongContent(song = song)
                        }
                        if (index < displaySongs.size - 1) {
                            IslandDivider()
                        }
                    }
                }
            }
        }

        if (showAlbums(selectedType) && albums.isNotEmpty()) {
            item {
                SearchSectionTitle(
                    title = "Álbumes",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                ResultsIsland(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val displayAlbums = albums.take(10)
                    displayAlbums.forEachIndexed { index, album ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenAlbum(album.detailLookup) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            CompactAlbumContent(album = album)
                        }
                        if (index < displayAlbums.size - 1) {
                            IslandDivider()
                        }
                    }
                }
            }
        }

        if (showArtists(selectedType) && artists.isNotEmpty()) {
            item {
                SearchSectionTitle(
                    title = "Artistas",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                ResultsIsland(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val displayArtists = artists.take(10)
                    displayArtists.forEachIndexed { index, artist ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenArtist(artist.detailLookup) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            CompactArtistContent(artist = artist)
                        }
                        if (index < displayArtists.size - 1) {
                            IslandDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyResultsContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin resultados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Intenta con otra búsqueda o categoría",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SoftRose, SoftRose.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ResultsIsland(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
private fun IslandDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
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
        SearchContent(state = SearchUiState.Idle, onAction = {})
    }
}

@Preview(name = "Search - Editing / Loading", showBackground = true)
@Composable
private fun SearchEditingLoadingPreview() {
    EssenceAppTheme {
        SearchContent(
            state = SearchUiState.Editing(isCategoriesLoading = true),
            onAction = {}
        )
    }
}

@Preview(name = "Search - Editing / Ready", showBackground = true)
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