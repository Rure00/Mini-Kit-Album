package com.rure.presentation.states

sealed class UiResult {
    data class Fail(val msg: String): UiResult()
    data object Loading: UiResult()
    data object Idle: UiResult()
}