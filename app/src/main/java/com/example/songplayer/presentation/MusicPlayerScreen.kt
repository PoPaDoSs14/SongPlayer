package com.example.songplayer.presentation

import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.songplayer.domain.Music
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@Composable
fun MusicPlayerScreen(music: Music?) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var tempFile: File? by remember { mutableStateOf(null) }

    LaunchedEffect(music) {
        mediaPlayer.reset()

        if (music?.musicLink != null) {
            val musicUri = Uri.parse(music.musicLink.toString())

            tempFile = saveFileFromUri(context.contentResolver, musicUri, context)

            tempFile?.let {
                mediaPlayer.setDataSource(it.absolutePath)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    duration = mediaPlayer.duration
                    mediaPlayer.start()
                    isPlaying = true
                }

                mediaPlayer.setOnCompletionListener {
                    isPlaying = false
                }
            }
        }

        while (isPlaying) {
            delay(1000)
            currentPosition = mediaPlayer.currentPosition
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
            tempFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
        }
    }

    if (music != null) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = music.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = music.artist,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LinearProgressIndicator(
                progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    // onPrevious()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Предыдущий трек")
                }

                IconButton(onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        mediaPlayer.start()
                    } else {
                        mediaPlayer.pause()
                    }
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Пауза" else "Воспроизведение"
                    )
                }

                IconButton(onClick = {
                    // onNext()
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Следующий трек")
                }
            }
        }
    } else {
        Text(text = "Музыка не найдена", style = MaterialTheme.typography.bodyLarge)
    }
}

fun saveFileFromUri(contentResolver: ContentResolver, uri: Uri, context: Context): File? {
    return try {
        val tempFile = File.createTempFile("music", ".mp3", context.cacheDir)
        tempFile.deleteOnExit()

        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val outputStream = FileOutputStream(tempFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        Log.e("MusicPlayer", "Error saving file from URI", e)
        null
    }
}