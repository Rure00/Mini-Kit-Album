package com.rure.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rure.presentation.components.AlbumCard
import com.rure.presentation.components.FeaturesSection

@Composable
fun HomeScreen(
    toAlbumScreen: (String) -> Unit,
    toLibraryScreen: () -> Unit,
) {
    val featuredAlbums = listOf<Album>()
    val onRegisterAlbum = {

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            HeroSection(
                onExploreLibrary = toLibraryScreen,
                onRegisterAlbum = onRegisterAlbum,
            )

            QuickRegisterSection(onRegisterAlbum = onRegisterAlbum)

            FeaturedAlbumsSection(
                albums = featuredAlbums,
                onViewAll = toLibraryScreen,
                onAlbumClick = toAlbumScreen,
            )

            FeaturesSection()
        }

        Footer()
    }
}

@Composable
private fun HeroSection(
    onExploreLibrary: () -> Unit,
    onRegisterAlbum: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GradientTitleText(text = "Your Music Collection")

        Text(
            text = "Register your albums, build your library, and enjoy your favorite music offline.\nFast, beautiful, and designed for music lovers.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 720.dp)
        )

        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onExploreLibrary,
                modifier = Modifier.height(48.dp),
            ) {
                Text("Explore Library")
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedButton(
                onClick = onRegisterAlbum,
                modifier = Modifier.height(48.dp),
            ) {
                Text("Register Album")
            }
        }
    }
}

@Composable
private fun GradientTitleText(text: String) {
    val brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
        )
    )
    Text(
        text = text,
        // TODO: brush = brush,
        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun QuickRegisterSection(
    onRegisterAlbum: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Quick Register",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RegistrationMethodCard(
                icon = Icons.Outlined.QrCode2,
                title = "Scan QR Code",
                description = "Scan the QR code on your album packaging to instantly add it to your library.",
                cta = "Scan Now",
                onClick = onRegisterAlbum,
            )

            RegistrationMethodCard(
                icon = Icons.Outlined.Pin,
                title = "Enter Code",
                description = "Don't have a QR code? Simply enter your 8-digit album verification code.",
                cta = "Enter Code",
                onClick = onRegisterAlbum,
            )
        }
    }
}

@Composable
private fun RegistrationMethodCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    cta: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }

            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)

            TextButton(onClick = onClick) {
                Text(cta)
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}

@Composable
private fun FeaturedAlbumsSection(
    albums: List<Album>,
    onViewAll: () -> Unit,
    onAlbumClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Featured Albums",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onViewAll) {
                Text("View All")
                Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 6.dp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                // TODO
                // Home 전체가 verticalScroll이라 grid 자체 스크롤 방지용 높이 고정 필요:
                // MVP: 아이템 수가 4개이니 계산형으로 대충 height 주는 게 제일 안전.
                .height(360.dp),
            userScrollEnabled = false,
        ) {
            items(albums, key = { it.id }) { album ->
                AlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album.id) }
                )
            }
        }
    }
}

@Composable
private fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "© 2024 KitAlbum. Your music, your library.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}