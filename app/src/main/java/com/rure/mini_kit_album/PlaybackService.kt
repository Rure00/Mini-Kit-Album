package com.rure.mini_kit_album

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager

@UnstableApi
class PlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var notificationManager: PlayerNotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build().apply {
            // 오디오 포커스/노이즈(이어폰 뽑힘) 대응 기본값은 웬만큼 됨.
            repeatMode = Player.REPEAT_MODE_OFF
        }

        mediaSession = MediaSession.Builder(this, player!!).build()

        ensureChannel()

        notificationManager =
            PlayerNotificationManager.Builder(this, NOTIF_ID, CHANNEL_ID)
                .setMediaDescriptionAdapter(DescriptionAdapter(this))
                .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationPosted(
                        notificationId: Int,
                        notification: android.app.Notification,
                        ongoing: Boolean
                    ) {
                        if (ongoing) startForeground(notificationId, notification)
                        else stopForeground(false)
                    }

                    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                        stopForeground(true)
                        stopSelf()
                    }
                })
                .build()
                .apply {
                    setPlayer(player)
                    setUseNextAction(false)
                    setUsePreviousAction(false)
                    setUseRewindAction(true)
                    setUseFastForwardAction(true)
                }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        notificationManager?.setPlayer(null)
        notificationManager = null

        mediaSession?.release()
        mediaSession = null

        player?.release()
        player = null

        super.onDestroy()
    }

    fun playUrl(url: String, autoPlay: Boolean = true) {
        player?.setMediaItem(MediaItem.fromUri(url))
        player?.prepare()
        player?.playWhenReady = autoPlay
    }

    private fun ensureChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        nm.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "playback"
        private const val NOTIF_ID = 1001
    }
}