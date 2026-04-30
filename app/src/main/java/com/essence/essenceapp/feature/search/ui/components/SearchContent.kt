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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.search.domain.model.Category
import com.essence.essenceapp.feature.search.domain.normalizeSearchType
import com.essence.essenceapp.feature.search.domain.showAlbums
import com.essence.essenceapp.feature.search.domain.showArtists
import com.essence.essenceapp.feature.search.domain.showSongs
import com.essence.essenceapp.feature.search.ui.SearchAction
import com.essence.essenceapp.feature.search.ui.SearchUiState
import com.essence.essenceapp.shared.ui.components.cards.album.GridAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CircleArtistContent
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import com.essence.essenceapp.shared.playback.mapper.toQueueItems
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    onAction: (SearchAction) -> Unit,
    onOpenSong: (PlaybackOpenRequest) -> Unit = {},
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
            onAction = onAction,
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
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchHeroOrb()

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Descubre tu sonido",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Busca canciones, artistas o albumes y arma tu universo musical.",
            style = MaterialTheme.typography.bodyMedium,
            color = PureWhite.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        InspirationChipsRow()
    }
}

@Composable
private fun SearchHeroOrb() {
    Box(
        modifier = Modifier.size(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.30f),
                            SoftRose.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.45f),
                        radius = 320f
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.22f),
                            MutedTeal.copy(alpha = 0.16f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.55f),
                            MutedTeal.copy(alpha = 0.30f),
                            LuxeGold.copy(alpha = 0.40f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = PureWhite.copy(alpha = 0.92f),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun InspirationChipsRow() {
    val items = listOf(
        "Pop" to SoftRose,
        "Indie" to MutedTeal,
        "Reggaeton" to LuxeGold,
        "Rock" to SoftRose,
        "R&B" to MutedTeal
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Outlined.TrendingUp,
            contentDescription = null,
            tint = PureWhite.copy(alpha = 0.45f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        items.take(3).forEach { (label, accent) ->
            InspirationChip(label = label, accent = accent)
        }
    }
}

@Composable
private fun InspirationChip(label: String, accent: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(
                width = 0.5.dp,
                color = accent.copy(alpha = 0.30f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = accent.copy(alpha = 0.95f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SearchEditingContent(
    modifier: Modifier = Modifier,
    state: SearchUiState.Editing,
    onAction: (SearchAction) -> Unit
) {
    val bottomClearance = LocalBottomBarClearance.current
    val selectedType = normalizeSearchType(state.form.type)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = bottomClearance + 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FadeSlideVisibility(
            visible = !state.isSubmitting,
            slideFromTop = true,
            enterDurationMillis = 320,
            exitDurationMillis = 200
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SearchFiltersIsland(state = state, onAction = onAction)
            }
        }

        FadeSlideVisibility(
            visible = state.isSubmitting,
            slideFromTop = true,
            enterDurationMillis = 360,
            exitDurationMillis = 200
        ) {
            SearchResultsSkeleton(
                showSongs = showSongs(selectedType),
                showAlbums = showAlbums(selectedType),
                showArtists = showArtists(selectedType),
                contentPadding = PaddingValues(top = 4.dp, bottom = 0.dp)
            )
        }
    }
}

@Composable
private fun FadeSlideVisibility(
    visible: Boolean,
    slideFromTop: Boolean = true,
    slideOffsetFraction: Int = 6,
    enterDurationMillis: Int = 300,
    exitDurationMillis: Int = 200,
    content: @Composable () -> Unit
) {
    val direction = if (slideFromTop) -1 else 1
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(enterDurationMillis, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            animationSpec = tween(enterDurationMillis, easing = FastOutSlowInEasing),
            initialOffsetY = { fullHeight -> direction * fullHeight / slideOffsetFraction }
        ),
        exit = fadeOut(
            animationSpec = tween(exitDurationMillis, easing = FastOutSlowInEasing)
        ) + slideOutVertically(
            animationSpec = tween(exitDurationMillis, easing = FastOutSlowInEasing),
            targetOffsetY = { fullHeight -> direction * fullHeight / slideOffsetFraction }
        )
    ) {
        content()
    }
}

@Composable
private fun SearchFiltersIsland(
    state: SearchUiState.Editing,
    onAction: (SearchAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GraphiteSurface.copy(alpha = 0.55f))
            .border(
                width = 0.6.dp,
                color = PureWhite.copy(alpha = 0.06f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to PureWhite.copy(alpha = 0.04f),
                        0.5f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.06f)
                    )
                )
        )

        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftRose.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = SoftRose,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Filtrar busqueda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "Afina por tipo de contenido",
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite.copy(alpha = 0.55f)
                    )
                }
            }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
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
                    text = "Cargando categorias...",
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
                text = "No hay categorias disponibles",
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
    onAction: (SearchAction) -> Unit,
    onOpenSong: (PlaybackOpenRequest) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit
) {
    val selectedType = normalizeSearchType(state.form.type)
    val songs = state.results.songs.orEmpty()
    val albums = state.results.albums.orEmpty()
    val artists = state.results.artists.orEmpty()
    val bottomClearance = LocalBottomBarClearance.current
    val hasResults = songs.isNotEmpty() || albums.isNotEmpty() || artists.isNotEmpty()

    if (!hasResults) {
        EmptyResultsContent(modifier = modifier)
        return
    }

    val lazyListState = rememberLazyListState()

    InfiniteScrollEffect(
        listState = lazyListState,
        canLoadMore = state.results.hasNextPage && !state.isLoadingNextPage,
        threshold = 4,
        onLoadMore = { onAction(SearchAction.LoadNextPage) }
    )

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = bottomClearance + 16.dp
        )
    ) {
        if (showSongs(selectedType) && songs.isNotEmpty()) {
            item {
                SearchSectionHeader(
                    title = "Canciones",
                    count = songs.size,
                    icon = Icons.Outlined.MusicNote,
                    accent = SoftRose,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                ResultsIsland(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val queueItems = songs.toQueueItems()
                    songs.forEachIndexed { index, song ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenSong(
                                        PlaybackOpenRequest(
                                            songLookup = song.detailLookup,
                                            queue = queueItems,
                                            startIndex = index,
                                            sourceKey = "search:${state.form.query}:${state.form.type}"
                                        )
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            CompactSongContent(
                                song = song,
                                showAddToPlaylistAction = true
                            )
                        }

                        if (index < songs.lastIndex) {
                            IslandDivider()
                        }
                    }
                }
            }
        }

        if (showAlbums(selectedType) && albums.isNotEmpty()) {
            item {
                SearchSectionHeader(
                    title = "Albumes",
                    count = albums.size,
                    icon = Icons.Outlined.Album,
                    accent = MutedTeal,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                CarouselIsland(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AlbumsCarousel(
                        albums = albums,
                        onOpenAlbum = onOpenAlbum
                    )
                }
            }
        }

        if (showArtists(selectedType) && artists.isNotEmpty()) {
            item {
                SearchSectionHeader(
                    title = "Artistas",
                    count = artists.size,
                    icon = Icons.Outlined.Person,
                    accent = LuxeGold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                CarouselIsland(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ArtistsCarousel(
                        artists = artists,
                        onOpenArtist = onOpenArtist
                    )
                }
            }
        }

        if (state.isLoadingNextPage) {
            item {
                SongRowsSkeleton(rowCount = 3)
            }
        } else if (state.results.hasNextPage) {
            item {
                LoadMoreFooter(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    hasNextPage = true,
                    isLoading = false,
                    onLoadMore = { onAction(SearchAction.LoadNextPage) }
                )
            }
        }
    }
}

@Composable
private fun AlbumsCarousel(
    albums: List<AlbumSimple>,
    onOpenAlbum: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(albums, key = { it.detailLookup }) { album ->
            Box(
                modifier = Modifier.clickable { onOpenAlbum(album.detailLookup) }
            ) {
                GridAlbumContent(album = album)
            }
        }
    }
}

@Composable
private fun ArtistsCarousel(
    artists: List<ArtistSimple>,
    onOpenArtist: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(artists, key = { it.detailLookup }) { artist ->
            Box(
                modifier = Modifier.clickable { onOpenArtist(artist.detailLookup) }
            ) {
                CircleArtistContent(artist = artist)
            }
        }
    }
}

@Composable
private fun LoadMoreFooter(
    modifier: Modifier = Modifier,
    hasNextPage: Boolean,
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    if (!hasNextPage && !isLoading) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SoftRose.copy(alpha = 0.08f))
            .border(
                width = 0.6.dp,
                color = SoftRose.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (hasNextPage && !isLoading) Modifier.clickable(onClick = onLoadMore)
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = SoftRose
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cargando mas resultados",
                    style = MaterialTheme.typography.labelMedium,
                    color = PureWhite.copy(alpha = 0.75f)
                )
            } else {
                Text(
                    text = "Cargar mas",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SoftRose
                )
            }
        }
    }
}

@Composable
private fun EmptyResultsContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MutedTeal.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GraphiteSurface.copy(alpha = 0.85f))
                    .border(
                        width = 1.dp,
                        color = PureWhite.copy(alpha = 0.06f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    tint = PureWhite.copy(alpha = 0.55f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sin resultados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Intenta con otras palabras o cambia la categoria seleccionada.",
            style = MaterialTheme.typography.bodyMedium,
            color = PureWhite.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchSectionHeader(
    title: String,
    count: Int,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(15.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(accent.copy(alpha = 0.10f))
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ResultsIsland(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GraphiteSurface.copy(alpha = 0.55f))
            .border(
                width = 0.6.dp,
                color = PureWhite.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to PureWhite.copy(alpha = 0.04f),
                        0.5f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.05f)
                    )
                )
        )
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            content()
        }
    }
}

@Composable
private fun CarouselIsland(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GraphiteSurface.copy(alpha = 0.55f))
            .border(
                width = 0.6.dp,
                color = PureWhite.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to PureWhite.copy(alpha = 0.04f),
                        0.5f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.05f)
                    )
                )
        )
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            content()
        }
    }
}

@Composable
private fun IslandDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 18.dp),
        thickness = 0.5.dp,
        color = PureWhite.copy(alpha = 0.05f)
    )
}

@Composable
private fun InfiniteScrollEffect(
    listState: LazyListState,
    canLoadMore: Boolean,
    threshold: Int,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember(listState, canLoadMore, threshold) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            canLoadMore && totalItems > 0 && lastVisible >= totalItems - threshold
        }
            .distinctUntilChanged()
            .filter { it }
    }

    LaunchedEffect(shouldLoadMore) {
        shouldLoadMore.collect { onLoadMore() }
    }
}

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
                    Category(label = "Albumes", value = "album"),
                    Category(label = "Artistas", value = "artist")
                )
            ),
            onAction = {}
        )
    }
}