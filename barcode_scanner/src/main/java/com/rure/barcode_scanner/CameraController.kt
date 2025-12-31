package com.rure.barcode_scanner

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.StateFlow

fun createCameraController(context: Context): CameraController = CameraControllerImpl(context)

interface CameraController {
    val cameraState: StateFlow<CameraUiState>

    fun getPreviewView(): View
    fun startCamera(lifecycleOwner: LifecycleOwner)

    suspend fun takePhoto(): String

    fun unbind()
}