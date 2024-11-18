package com.example.songplayer.presentation

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.songplayer.R
import java.io.File
import java.io.IOException

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
        startForeground(notificationId, showNotification("Music is playing..."))
        val action = intent?.getStringExtra("action")

        when (action) {
            "PLAY" -> {
                val musicUri = intent.getStringExtra("music_uri")
                playMusic(musicUri)
                showNotification("Playing music")
            }
            "PAUSE" -> {
                pauseMusic()
                showNotification("Music paused")
            }
            "STOP" -> {
                stopMusic()
                showNotification("Music stopped")
            }
        }

        return START_STICKY
    }

    private fun playMusic(musicUri: String?) {
        if (musicUri == null) {
            Log.e("MusicService", "Music URI is null")
            return
        }

        Log.d("MusicService", "Playing music from URI: $musicUri")

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.reset()
            try {
                val uri = Uri.parse(musicUri)

                mediaPlayer.setDataSource(this, uri)
                mediaPlayer.prepareAsync()

                mediaPlayer.setOnPreparedListener {
                    it.start()
                    Log.d("MusicService", "Music started successfully")
                    showNotification("Playing music")
                }

                mediaPlayer.setOnCompletionListener {
                    Log.d("MusicService", "Music playback completed.")
                }

                showNotification("Playing music")
            } catch (e: IOException) {
                Log.e("MusicService", "Error setting data source", e)
            } catch (e: Exception) {
                Log.e("MusicService", "Unexpected error", e)
            }
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            showNotification("Music paused")
        }
    }

    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        stopForeground(true)
        stopSelf()
    }

    @SuppressLint("ForegroundServiceType")
    private fun showNotification(playStatus: String): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "music_player_channel"
        val channelName = "Music Player"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }
        }

        val stopIntent = Intent(this, MusicService::class.java).apply {
            action = "STOP_ACTION"
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText(playStatus)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_launcher_background, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        notificationManager.notify(notificationId, notification)

        return notification
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}