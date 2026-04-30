package com.essence.essenceapp.feature.playlist.ui.addsongs.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.ui.addsongs.PlaylistAddSongsAction
import com.essence.essenceapp.feature.playlist.ui.addsongs.PlaylistAddSongsUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PlaylistAddSongsContent(
    modifier: Modifier = Modifier,
    state: PlaylistAddSongsUiState,
    query: String,
    addedDuringSession: Int,
    onAction: (PlaylistAddSongsAction) -> Unit,
    onDone: () -> Unit
) {
    val bottomClearance = LocalBottomBarClearance.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchField(
                query = query,
                onQueryChanged = { onAction(PlaylistAddSongsAction.QueryChanged(it)) },
                onSearch = { onAction(PlaylistAddSongsAction.Submit) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when (state) {
                PlaylistAddSongsUiState.Idle -> IdleContent()
                PlaylistAddSongsUiState.Loading -> SearchLoadingShimmer(bottomClearance = bottomClearance)
                is PlaylistAddSongsUiState.Success -> {
                    if (state.songs.isEmpty()) {
                        EmptyResultsContent()
                    } else {
                        SongsResultList(
                            songs = state.songs,
                            addedSongKeys = state.addedSongKeys,
                            addingSongKeys = state.addingSongKeys,
                            hasNextPage = state.hasNextPage,
                            isLoadingNextPage = state.isLoadingNextPage,
                            onAddSong = { key -> onAction(PlaylistAddSongsAction.AddSong(key)) },
                            onLoadNextPage = { onAction(PlaylistAddSongsAction.LoadNextPage) },
                            bottomClearance = bottomClearance + if (addedDuringSession > 0) 80.dp else 0.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is PlaylistAddSongsUiState.Error -> AppErrorState(
                    message = state.message,
                    title = "Error al buscar",
                    onRetry = { onAction(PlaylistAddSongsAction.Submit) }
                )
            }
        }

        AnimatedVisibility(
            visible = addedDuringSession > 0,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = bottomClearance + 16.dp
                )
        ) {
            DoneFab(
                count = addedDuringSession,
                onClick = onDone
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Buscar canciones...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            onSearch()
        }),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MutedTeal.copy(alpha = 0.55f),
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.22f)
        )
    )
}

@Composable
private fun IdleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MutedTeal.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.22f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MutedTeal.copy(alpha = 0.65f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Busca canciones para agregar",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Escribe el nombre de una canción o artista.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.50f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchLoadingShimmer(bottomClearance: Dp) {
    val transition = rememberInfiniteTransition(label = "addsongs_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    val baseColor = PureWhite.copy(alpha = alpha)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = bottomClearance + 8.dp)
        ) {
            repeat(6) { index ->
                ShimmerRow(base = baseColor)
                if (index < 5) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 0.5.dp,
                        color = PureWhite.copy(alpha = 0.04f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerRow(base: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(base)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(base)
            )
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(base)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(base)
        )
    }
}

@Composable
private fun EmptyResultsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.MusicNote,
                    contentDescription = null,
                    tint = PureWhite.copy(alpha = 0.32f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "No se encontraron canciones",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Intenta con otro término de búsqueda.",
            style = MaterialTheme.typography.bodySmall,
            color = PureWhite.copy(alpha = 0.48f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SongsResultList(
    songs: List<SongSimple>,
    addedSongKeys: Set<String>,
    addingSongKeys: Set<String>,
    hasNextPage: Boolean,
    isLoadingNextPage: Boolean,
    onAddSong: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    bottomClearance: Dp,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            lastVisible >= totalItems - 3
        }
            .distinctUntilChanged()
            .collect { isNearEnd ->
                if (isNearEnd && hasNextPage && !isLoadingNextPage) {
                    onLoadNextPage()
                }
            }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.40f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.05f)),
        shape = MaterialTheme.shapes.large
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 4.dp, bottom = bottomClearance + 4.dp)
        ) {
            items(songs, key = { it.hlsMasterKey }) { song ->
                val songKey = song.hlsMasterKey
                val isAdded = songKey in addedSongKeys
                val isAdding = songKey in addingSongKeys

                SongResultItem(
                    song = song,
                    isAdded = isAdded,
                    isAdding = isAdding,
                    onAdd = { onAddSong(songKey) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                )
            }

            if (isLoadingNextPage) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MutedTeal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SongResultItem(
    song: SongSimple,
    isAdded: Boolean,
    isAdding: Boolean,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CompactSongContent(song = song, showAddToPlaylistAction = false)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = if (isAdded) MutedTeal.copy(alpha = 0.14f)
            else SoftRose.copy(alpha = 0.10f),
            border = BorderStroke(
                0.5.dp,
                if (isAdded) MutedTeal.copy(alpha = 0.30f)
                else SoftRose.copy(alpha = 0.20f)
            )
        ) {
            IconButton(
                onClick = onAdd,
                enabled = !isAdded && !isAdding
            ) {
                AnimatedContent(
                    targetState = when {
                        isAdding -> "loading"
                        isAdded -> "done"
                        else -> "add"
                    },
                    transitionSpec = {
                        (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                    },
                    label = "add_button"
                ) { state ->
                    when (state) {
                        "loading" -> CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = SoftRose
                        )
                        "done" -> Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Agregada",
                            tint = MutedTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint = SoftRose,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoneFab(
    count: Int,
    onClick: () -> Unit
) {
    val label = if (count == 1) "Listo · 1 agregada" else "Listo · $count agregadas"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.95f),
                            Color(0xFFBB4477).copy(alpha = 0.92f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MidnightBlack.copy(alpha = 0.92f),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MidnightBlack.copy(alpha = 0.92f)
                )
            }
        }
    }
}
