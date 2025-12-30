package com.rure.presentation.components


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rure.presentation.ui.theme.LightGray
import com.rure.presentation.ui.theme.White
import com.rure.presentation.ui.theme.mainGradientBrush
import com.rure.presentation.ui.theme.primary
import com.rure.presentation.ui.theme.surface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


enum class RegisterMethod { QR, CODE }

@Composable
fun RegisterAlbumDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    onRegistered: () -> Unit = {},
) {
    if (!open) return

    val density = LocalDensity.current
    val screenHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }

    var method by remember { mutableStateOf<RegisterMethod?>(null) }
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun reset() {
        method = null
        code = ""
        isLoading = false
    }

    // ======================================================================================

    Dialog(
        onDismissRequest = {
            reset()
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true
        )
    ) {
        BackHandler {
            when (method) {
                null -> onDismiss()
                else -> { method = null }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 20.dp)
                .background(color = surface, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 30.dp, horizontal = 30.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Register Album",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Add a new album to your library using a QR code or verification code.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightGray
                )


                if (method == null) {
                    Spacer(Modifier.height(20.dp))
                    MethodPicker(
                        onPickQr = { method = RegisterMethod.QR },
                        onPickCode = { method = RegisterMethod.CODE },
                    )
                } else {
                    TextButton(
                        onClick = { method = null },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Choose different method")
                    }

                    when (method) {
                        RegisterMethod.QR -> {
                            QrSection(
                                isLoading = isLoading,
                                onScanClick = {
                                    // TODO: 카메라 연결
                                    scope.launch {
                                        isLoading = true
                                        delay(1000)
                                        isLoading = false
                                        onRegistered()
                                        reset()
                                        onDismiss()
                                    }
                                }
                            )
                        }

                        RegisterMethod.CODE -> {
                            CodeSection(
                                code = code,
                                onCodeChange = { code = it },
                                isLoading = isLoading,
                                onCancel = { method = null },
                                onSubmit = {
                                    scope.launch {
                                        isLoading = true
                                        delay(1000) // simulate
                                        isLoading = false
                                        onRegistered()
                                        reset()
                                        onDismiss()
                                    }
                                }
                            )
                        }

                        null -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun MethodPicker(
    onPickQr: () -> Unit,
    onPickCode: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MethodCard(
            title = "Scan QR Code",
            icon = Icons.Outlined.QrCode2,
            desc = "Use your device camera to scan the QR code from your album packaging.",
            onClick = onPickQr,
        )

        MethodCard(
            title = "Enter Code",
            icon = Icons.Outlined.Pin,
            desc = "Enter your 8-digit album verification code manually.",
            onClick = onPickCode,
        )
    }
}

@Composable
private fun MethodCard(
    title: String,
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)


    Column(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, primary, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(mainGradientBrush),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = White)
        }

        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = White)
        Spacer(Modifier.height(6.dp))
        Text(desc, style = MaterialTheme.typography.bodySmall, color = LightGray)
    }
}

@Composable
private fun QrSection(
    isLoading: Boolean,
    onScanClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("QR Code Scanner", fontWeight = FontWeight.SemiBold, color = White)

        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .border(2.dp, primary.copy(alpha = 0.7f), shape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,
                    onClick = { /* TODO: 스캔 시작 */ }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("QR", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Tap to scan QR code", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        GradientButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            gradientBrush = mainGradientBrush,
            onClick = onScanClick
        ) {
            Text(
                text = if (isLoading) "Registering..." else "Scan Code",
            )
        }
    }
}

@Composable
private fun CodeSection(
    code: String,
    onCodeChange: (String) -> Unit,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Verification Code", fontWeight = FontWeight.SemiBold, color = White)

        OutlinedTextField(
            value = code,
            onValueChange = { input ->
                val next = input.uppercase().take(8)
                onCodeChange(next)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Enter 8-digit code (e.g., ABC12345)", maxLines = 1) },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                letterSpacing = MaterialTheme.typography.titleMedium.letterSpacing
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Ascii
            ),
            colors = OutlinedTextFieldDefaults.colors().copy(focusedTextColor = White, unfocusedTextColor = White)
        )

        Text(
            text = "You can find this code on your album packaging or receipt.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = surface,shape = RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, primary), RoundedCornerShape(14.dp))
        ) {
            Text(
                text = "Ownership Verification: By entering this code, you confirm that you own the physical album and agree to our terms of service.",
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) { Text("Cancel") }

            GradientButton(
                modifier = Modifier.weight(1f),
                gradientBrush = mainGradientBrush,
                enabled = code.length >= 8 && !isLoading,
                onClick = onSubmit
            ) {
                Text(
                    text = if (isLoading) "Registering..." else "Register Album",
                )
            }
        }
    }
}
