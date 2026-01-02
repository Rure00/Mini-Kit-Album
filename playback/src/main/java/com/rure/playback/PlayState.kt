package com.rure.playback

sealed class PlayState {
    data object Loading: PlayState()
    data object Idle : PlayState()
    data object Playing : PlayState()
    data object Paused: PlayState()
}
