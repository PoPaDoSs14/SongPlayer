package com.example.songplayer.presentation

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.songplayer.R

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val notificationChannelId = "music_service_channel"
    private val notificationId = 1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")

        when (action) {
            "PLAY" -> {
                val musicResId = intent.getIntExtra("music_res_id", -1)
                if (musicResId != -1) {
                    playMusic(musicResId)
                    showNotification()
                }
            }
            "PAUSE" -> pauseMusic()
            "STOP" -> stopMusic()
        }

        return START_STICKY
    }

    private fun playMusic(resId: Int) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.reset()
            mediaPlayer = MediaPlayer.create(this, resId)
            mediaPlayer.start()
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    private fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        stopForeground(true)
        stopSelf()
    }

    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Music Service")
            .setContentText("Music is playing")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        startForeground(notificationId, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
        mediaPlayer.release()
    }
}