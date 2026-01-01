package com.rure.presentation.screen

import android.net.Uri
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import com.rure.presentation.components.GradientButton
import com.rure.presentation.formatDate
import com.rure.presentation.formatDuration
import com.rure.presentation.parseYear
import com.rure.presentation.ui.theme.LightGray
import com.rure.presentation.ui.theme.White
import com.rure.presentation.ui.theme.mainGradientBrush
import com.rure.presentation.ui.theme.primary
import com.rure.presentation.ui.theme.surface
import com.rure.presentation.viewmodels.AlbumDetailViewModel
import com.rure.presentation.viewmodels.AlbumViewModel
import com.rure.presentation.viewmodels.TrackPlayViewModel

private val Pink = Color(0xFFCF22C5)
private val Purple = Color(0xFF857AD0)

private enum class AlbumTab { TRACKS, MUSIC, PHOTOS, VIDEOS }

@Composable
fun AlbumScreen(
    albumDetailViewModel: AlbumDetailViewModel = hiltViewModel(),
    trackPlayViewModel: TrackPlayViewModel = hiltViewModel(),
    onBackToLibrary: () -> Unit,
) {
    val selectedAlbum by albumDetailViewModel.selectedAlbum.collectAsStateWithLifecycle()
    var activeTab by rememberSaveable { mutableStateOf(AlbumTab.TRACKS) }

    val onPlayMusic: (Track) -> Unit = {
        trackPlayViewModel.play(it)
    }
    val onDownload: (Track) -> Unit = {

    }


    // ======================================================================================






    // ======================================================================================

    if (selectedAlbum == null) {
        AlbumNotFound(
            onBackToLibrary = onBackToLibrary,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val downloadedCount = remember(selectedAlbum) { selectedAlbum!!.tracks.count { it.downloaded } }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            AlbumInfoSection(
                album = selectedAlbum!!,
                downloadedCount = downloadedCount,
                onDownloadAlbum = { /* TODO */ },
            )

            Spacer(Modifier.height(16.dp))

            AlbumTabs(
                activeTab = activeTab,
                onTabChange = { activeTab = it }
            )

            Spacer(Modifier.height(12.dp))
        }

        when (activeTab) {
            AlbumTab.TRACKS -> {
                items(selectedAlbum!!.tracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        onPlay = { onPlayMusic(track) },
                        onDownload = { onDownload(track) }
                    )
                }
            }
            AlbumTab.MUSIC -> {
                item {
                    InfoCard(
                        icon = Icons.Outlined.MusicNote,
                        title = "Music Content",
                        body = "All ${selectedAlbum!!.tracks.size} tracks are available for streaming and offline download."
                    )
                }
            }
            AlbumTab.PHOTOS -> {
                MediaGrid(
                    contents = selectedAlbum!!.images?.map { uri -> Uri.parse(uri) } ?: listOf(),
                    backgroundBrush = Brush.linearGradient(
                        listOf(
                            Pink.copy(alpha = 0.20f),
                            Purple.copy(alpha = 0.20f)
                        )
                    ),
                )
            }

            AlbumTab.VIDEOS -> {
                MediaGrid(
                    contents = selectedAlbum!!.videos?.map { uri -> Uri.parse(uri) } ?: listOf(),
                    backgroundBrush = Brush.linearGradient(
                        listOf(
                            Pink.copy(alpha = 0.20f),
                            Purple.copy(alpha = 0.20f)
                        )
                    ),
                )

            }
        }


    }
}

@Composable
private fun AlbumNotFound(
    onBackToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackToLibrary) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Back to Library")
        }

        Spacer(Modifier.height(24.dp))

        Text("Album Not Found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("This album doesn't exist.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackToLibrary) { Text("Go Back") }
    }
}

@Composable
private fun AlbumInfoSection(
    album: Album,
    downloadedCount: Int,
    onDownloadAlbum: () -> Unit,
) {
    val year = remember(album.releaseDate) { parseYear(album.releaseDate) }
    val releaseText = remember(album.releaseDate) { formatDate(album.releaseDate) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(surface),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(album.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = album.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(Modifier.height(12.dp))

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                gradientBrush = mainGradientBrush,
                onClick = onDownloadAlbum
            ) {
                Text(text = "Download Album", color = White, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "${album.genre} • $year",
                style = MaterialTheme.typography.labelMedium,
                color = LightGray
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = album.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = album.artist,
                style = MaterialTheme.typography.titleMedium,
                color = LightGray
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = album.description,
                style = MaterialTheme.typography.bodyMedium,
                color = LightGray
            )

            Spacer(Modifier.height(14.dp))

            // Stats cards 3개
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Tracks",
                    value = "${album.tracks.size}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Downloaded",
                    value = "$downloadedCount/${album.tracks.size}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Released",
                    value = releaseText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .background(color = surface, shape = shape)
            .border(1.dp, primary, shape)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = LightGray)
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AlbumTabs(
    activeTab: AlbumTab,
    onTabChange: (AlbumTab) -> Unit,
) {
    val tabs = listOf(
        AlbumTab.TRACKS to Icons.Outlined.MusicNote,
        AlbumTab.MUSIC to Icons.Outlined.LibraryMusic,
        AlbumTab.PHOTOS to Icons.Outlined.Image,
        AlbumTab.VIDEOS to Icons.Outlined.Videocam
    )

    TabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == activeTab },
        containerColor = Color.Transparent,
        contentColor = LightGray,
    ) {
        tabs.forEachIndexed { _, (tab, icon) ->
            val isSelected = (tab == activeTab)
            Tab(
                selected = isSelected,
                onClick = { onTabChange(tab) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val brush = if (isSelected) mainGradientBrush
                                    else Brush.linearGradient(listOf(LightGray, LightGray))

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(brush, blendMode = BlendMode.SrcAtop)
                                    }
                                },
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.SemiBold,
                            style = TextStyle(brush = brush)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent, shape = shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPlay) {
                Icon(
                    Icons.Outlined.PlayArrow,
                    contentDescription = "Play",
                    tint = White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    track.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    formatDuration(track.durationSec),
                    style = MaterialTheme.typography.bodySmall,
                    color = White
                )
            }

            if (track.downloaded) {
                val chipShape = RoundedCornerShape(4.dp)
                Box(
                    modifier = Modifier
                        .background(Color.Transparent, chipShape)
                        .border(1.dp, primary, chipShape)
                        .padding(vertical = 3.dp, horizontal = 5.dp)
                ) { Text("Downloaded", color = LightGray) }
                Spacer(Modifier.width(8.dp))
            }

            IconButton(onClick = onDownload) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Download",
                    tint = White
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    body: String,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = surface, shape = shape)
            .border(1.dp, primary, shape),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = White
            )
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = White)
            Spacer(Modifier.height(8.dp))
            Text(body, color = LightGray)
        }
    }
}

private fun LazyListScope.MediaGrid(
    contents: List<Uri>,
    backgroundBrush: Brush,
) {
    items(contents, key = { it } ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(backgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

