package com.rure.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rure.domain.entities.Album
import com.rure.presentation.components.AlbumCard
import com.rure.presentation.components.FeaturesSection
import com.rure.presentation.components.GradientButton
import com.rure.presentation.components.RegisterAlbumDialog
import com.rure.presentation.states.AlbumIntent
import com.rure.presentation.states.UiResult
import com.rure.presentation.states.UiResult.*
import com.rure.presentation.ui.theme.Black
import com.rure.presentation.ui.theme.LightGray
import com.rure.presentation.ui.theme.Typography
import com.rure.presentation.ui.theme.White
import com.rure.presentation.ui.theme.mainGradient
import com.rure.presentation.ui.theme.mainGradientBrush
import com.rure.presentation.ui.theme.surface
import com.rure.presentation.viewmodels.AlbumViewModel

@Composable
fun HomeScreen(
    albumViewModel: AlbumViewModel = hiltViewModel(),
    toAlbumScreen: (String) -> Unit,
    toLibraryScreen: () -> Unit,
) {
    val uiResult by albumViewModel.uiResult.collectAsStateWithLifecycle()
    val featuredAlbums by albumViewModel.album.collectAsStateWithLifecycle()

    var registerModalState by remember { mutableStateOf(false) }
    val onOpenRegisterModal: () -> Unit = { registerModalState = true }
    val onRegisterAlbum: (String) -> Unit = { code ->
        albumViewModel.emitAlbumIntent(AlbumIntent.RegisterAlbum(code))
    }

    // =================================================================================


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
                onOpenRegisterModal = onOpenRegisterModal,
            )

            QuickRegisterSection(onOpenRegisterModal = onOpenRegisterModal)

            FeaturedAlbumsSection(
                albums = featuredAlbums,
                onViewAll = toLibraryScreen,
                onAlbumClick = toAlbumScreen,
            )

            FeaturesSection()
        }

        Footer()
    }

    RegisterAlbumDialog(
        open = registerModalState,
        onDismiss = { registerModalState = false },
    )

    if (uiResult is Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(999f)
                .background(Black.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun HeroSection(
    onExploreLibrary: () -> Unit,
    onOpenRegisterModal: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Your Music Collection",
            style = TextStyle(
                brush = mainGradientBrush,
            ),
            fontSize = Typography.displayMedium.fontSize,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Register your albums, build your library, and enjoy your favorite music offline.\nFast, beautiful, and designed for music lovers.",
            style = MaterialTheme.typography.bodyLarge,
            color = LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 720.dp)
        )

        Column(
            modifier = Modifier.padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GradientButton(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                gradientBrush = mainGradientBrush,
                onClick = onExploreLibrary
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Explore Library",)
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            OutlinedButton(
                onClick = onOpenRegisterModal,
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Text(
                    text = "Register Album",
                    color = White
                )
            }
        }
    }
}

@Composable
private fun QuickRegisterSection(
    onOpenRegisterModal: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Quick Register",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = White
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RegistrationMethodCard(
                icon = Icons.Outlined.QrCode2,
                title = "Scan QR Code",
                description = "Scan the QR code on your album packaging to instantly add it to your library.",
                cta = "Scan Now",
                onClick = onOpenRegisterModal,
            )

            RegistrationMethodCard(
                icon = Icons.Outlined.Pin,
                title = "Enter Code",
                description = "Don't have a QR code? Simply enter your 8-digit album verification code.",
                cta = "Enter Code",
                onClick = onOpenRegisterModal,
            )
        }
    }
}

@Composable
private fun RegistrationMethodCard(
    icon: ImageVector,
    title: String,
    description: String,
    cta: String,
    onClick: () -> Unit,
) {
    val indication = LocalIndication.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = surface, shape = RoundedCornerShape(16.dp))
            .clickable(
                onClick = onClick,
                indication = indication,
                interactionSource = remember { MutableInteractionSource() }
            ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(mainGradientBrush),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }

            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = White)
            Text(description, color = White)

            TextButton(onClick = onClick, colors = ButtonDefaults.textButtonColors().copy(contentColor = White)) {
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
                color = White,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onViewAll, colors = ButtonDefaults.textButtonColors().copy(contentColor = White)) {
                Text("View All")
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 6.dp))
            }
        }

        val albumItems = albums.take(4)
        if (albumItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Register new Album!",
                    color = LightGray,
                    textAlign = TextAlign.Center,
                    fontSize = Typography.bodyMedium.fontSize
                )
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                albumItems.forEach {
                    AlbumCard(
                        album = it,
                        onClick = { onAlbumClick(it.id) }
                    )
                }
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
            .background(surface.copy(alpha = 0.35f))
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "© 2024 KitAlbum. Your music, your library.",
            color = LightGray
        )
    }
}