package com.rure.barcode_scanner

import android.content.Context
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.core.Preview
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class CameraControllerImpl(private val context: Context): CameraController {
    private val _cameraState = MutableStateFlow<CameraUiState>(CameraUiState.NotReady)
    override val cameraState: StateFlow<CameraUiState> = _cameraState.asStateFlow()

    private lateinit var cameraController: LifecycleCameraController
    private lateinit var previewView: PreviewView
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var cameraExecutor: ExecutorService


    override fun getPreviewView(): View  = previewView

    override fun startCamera(lifecycleOwner: LifecycleOwner, ) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        val mainExecutor = ContextCompat.getMainExecutor(context)
        cameraExecutor = Executors.newSingleThreadExecutor()

        previewView = PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }

        cameraController = LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(androidx.camera.view.CameraController.IMAGE_ANALYSIS)

            setImageAnalysisAnalyzer(
                cameraExecutor,
                MlKitAnalyzer(
                    listOf(barcodeScanner),
                    COORDINATE_SYSTEM_VIEW_REFERENCED,
                    mainExecutor
                ) { result: MlKitAnalyzer.Result? ->
                    val barcodeResults = result?.getValue(barcodeScanner).orEmpty()
                    if (barcodeResults.isEmpty()) return@MlKitAnalyzer

                    val rawResult = mutableListOf<String>()
                    barcodeResults.forEach { barcode ->
                        barcode.rawValue?.let { rawResult.add(it) }
                    }

                    _cameraState.value = CameraUiState.Captured(rawResult)
                }
            )
        }

        previewView.controller = cameraController
        cameraController.bindToLifecycle(lifecycleOwner)


        _cameraState.value = CameraUiState.Ready
    }

    override suspend fun takePhoto(): String {
        TODO("Not yet implemented")
    }

    override fun unbind() {
        if (cameraState.value is CameraUiState.NotReady) return

        try {
            cameraController.unbind()
            cameraExecutor.shutdown()
            barcodeScanner.close()
        } catch (e: Exception) {
            Log.e("CameraControllerImpl", "Error unbinding camera", e)
        }
    }
}