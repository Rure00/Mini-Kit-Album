package com.rure.presentation

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


private val permissions = listOf(Manifest.permission.CAMERA)

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun permissionState(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
) = rememberMultiplePermissionsState(permissions) {
    if(!it.containsValue(false)) {
        onGranted()
    } else {
        onDenied()
    }
}