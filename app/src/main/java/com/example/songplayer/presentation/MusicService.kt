package com.example.songplayer.presentation

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
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
        val action = intent?.getStringExtra("action")

        when (action) {
            "PLAY" -> {
                val musicUri = intent.getStringExtra("music_uri")
                playMusic(musicUri)
            }
            "PAUSE" -> pauseMusic()
            "STOP" -> stopMusic()
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
    private fun showNotification(content: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Music Service")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        startForeground(notificationId, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}