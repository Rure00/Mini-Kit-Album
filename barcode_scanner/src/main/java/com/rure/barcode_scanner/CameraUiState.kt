package com.rure.barcode_scanner


sealed class CameraUiState {
    data object NotReady: CameraUiState()
    data object Ready: CameraUiState()

    data class Captured(val rawBarcodes: List<String>): CameraUiState()
}