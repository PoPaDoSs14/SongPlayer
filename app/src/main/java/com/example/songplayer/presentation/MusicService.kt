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
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")
        startForeground(notificationId, showNotification("Service starting..."))

        when (action) {
            "PLAY" -> {
                val musicUri = intent.getStringExtra("music_uri")
                playMusic(musicUri)
            }
            "PAUSE" -> {
                pauseMusic()
            }
            "STOP" -> {
                stopMusic()
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
                    showNotification("Playing music") // Обновление уведомления
                    Log.d("MusicService", "Music started successfully")
                }

                mediaPlayer.setOnCompletionListener {
                    Log.d("MusicService", "Music playback completed.")
                    showNotification("Music completed") // Уведомление по завершении
                }
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

    private fun showNotification(playStatus: String): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(notificationChannelId)
            if (existingChannel == null) {
                val channel = NotificationChannel(notificationChannelId, "Music Service Channel", NotificationManager.IMPORTANCE_LOW)
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

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Music Player")
            .setContentText(playStatus)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_launcher_background, "Stop", stopPendingIntent)
            .setOngoing(true) // Уведомление будет постоянно, пока сервис работает
            .build()

        notificationManager.notify(notificationId, notification)
        return notification
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelExists = notificationManager.getNotificationChannel(notificationChannelId) != null
            if (!channelExists) {
                val channel = NotificationChannel(notificationChannelId, "Music Service Channel", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
                Log.d("MusicService", "Notification channel created")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}