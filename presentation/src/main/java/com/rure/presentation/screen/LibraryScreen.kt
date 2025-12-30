package com.rure.presentation.screen

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import com.rure.domain.entities.Album
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rure.presentation.components.AlbumCard
import com.rure.presentation.ui.theme.LightGray
import com.rure.presentation.ui.theme.White
import com.rure.presentation.ui.theme.mainGradient
import com.rure.presentation.ui.theme.mainGradientBrush
import com.rure.presentation.ui.theme.primary
import com.rure.presentation.ui.theme.surface


@Composable
fun LibraryScreen(
    searchAlbums: (String) -> List<Album>,
    onAlbumClick: (Album) -> Unit = {},
) {
    val albums by remember { mutableStateOf(listOf<Album>()) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filterDownloaded by rememberSaveable { mutableStateOf(false) }
    var selectedGenres by rememberSaveable { mutableStateOf(setOf<String>()) }


    val allGenres = remember(albums) { albums.map { it.genre }.distinct().sorted() }

    val base = remember(searchQuery, albums) {
        if (searchQuery.isNotBlank()) searchAlbums(searchQuery) else albums
    }

    val filtered = remember(base, filterDownloaded, selectedGenres) {
        base
            .let { list -> if (filterDownloaded) list.filter { it.downloaded } else list }
            .let { list ->
                if (selectedGenres.isNotEmpty()) list.filter { it.genre in selectedGenres } else list
            }
    }

    val downloadedCount = remember(filtered) { filtered.count { it.downloaded } }



    // ===================================================================================



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${filtered.size} album${if (filtered.size != 1) "s" else ""} • $downloadedCount downloaded",
                style = MaterialTheme.typography.bodyMedium,
                color = LightGray
            )

            Spacer(Modifier.height(20.dp))

            // Search & Filters
            SearchAndFilters(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                filterDownloaded = filterDownloaded,
                onToggleDownloaded = { filterDownloaded = !filterDownloaded },
                downloadedCount = downloadedCount,
                allGenres = allGenres,
                selectedGenres = selectedGenres,
                onToggleGenre = { genre ->
                    selectedGenres =
                        if (genre in selectedGenres) selectedGenres - genre
                        else selectedGenres + genre
                }
            )

            Spacer(Modifier.height(16.dp))

            if (filtered.isNotEmpty()) {
                LazyVerticalGrid(
                    state = rememberLazyGridState(),
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }) { album ->
                        AlbumCard(
                            album = album,
                            onClick = { onAlbumClick(album) }
                        )
                    }
                }
            } else {
                EmptyState(
                    hasQuery = searchQuery.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilters(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterDownloaded: Boolean,
    onToggleDownloaded: () -> Unit,
    downloadedCount: Int,
    allGenres: List<String>,
    selectedGenres: Set<String>,
    onToggleGenre: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
            placeholder = { Text(text = "Search albums, artists, genres...", maxLines = 1) },
            colors = OutlinedTextFieldDefaults
                .colors()
                .copy(focusedTextColor = White, unfocusedTextColor = White)
        )

        // Filter row
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = null,
                    tint = LightGray
                )
                Text(
                    text = "Filter:",
                    style = MaterialTheme.typography.labelLarge,
                    color = LightGray,
                    fontWeight = FontWeight.Medium
                )
            }

            GradientFilterChip(
                modifier = Modifier.fillMaxWidth(),
                selected = filterDownloaded,
                text = "Downloaded ($downloadedCount)",
                onClick = onToggleDownloaded
            )

            // Genres
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allGenres.forEach { genre ->
                    GradientFilterChip(
                        selected = genre in selectedGenres,
                        text = genre,
                        onClick = { onToggleGenre(genre) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientFilterChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(999.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .wrapContentHeight()
            .clip(shape)
            .then(
                if (selected) {
                    Modifier.background(mainGradientBrush)
                } else {
                    Modifier.background(color = Color.Transparent, shape = shape)
                        .border(1.dp, primary, shape)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(vertical = 5.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = White, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun EmptyState(
    hasQuery: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .border(2.dp, primary, shape)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = LightGray
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No albums found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = LightGray
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (hasQuery) "Try adjusting your search terms" else "Start by registering an album",
            style = MaterialTheme.typography.bodyMedium,
            color = LightGray
        )
    }
}