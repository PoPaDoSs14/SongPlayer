package com.example.songplayer.presentation

import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.example.songplayer.domain.Music
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MusicPlayerScreen(music: Music?) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var tempFile: File? by remember { mutableStateOf(null) }


    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(music) {
        mediaPlayer.reset()

        music?.musicLink?.let { musicLink ->
            val musicUri = Uri.parse(musicLink.toString())

            tempFile = saveFileFromUri(context, musicUri, music)

            tempFile?.let { file ->
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.prepareAsync()

                mediaPlayer.setOnPreparedListener {
                    duration = mediaPlayer.duration
                    if (isPlaying) {
                        mediaPlayer.start()
                    }


                    job = scope.launch {
                        while (isPlaying) {
                            if (mediaPlayer.isPlaying) {
                                currentPosition = mediaPlayer.currentPosition
                            }
                            delay(1000)
                        }
                    }
                }

                mediaPlayer.setOnCompletionListener {
                    isPlaying = false
                    job?.cancel()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
            job?.cancel()
            tempFile?.delete()
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = music?.name ?: "Unknown Track", style = MaterialTheme.typography.headlineLarge)
        Text(text = music?.artist ?: "Unknown Artist", style = MaterialTheme.typography.titleMedium)

        LinearProgressIndicator(
            progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
            modifier = Modifier.fillMaxWidth().height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* onPrevious() */ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Предыдущий трек")
            }

            IconButton(onClick = {
                if (isPlaying) {
                    mediaPlayer.pause()
                    job?.cancel()
                } else {
                    mediaPlayer.start()
                    job = scope.launch {
                        while (isPlaying) {
                            currentPosition = mediaPlayer.currentPosition
                            delay(1000)
                        }
                    }
                }
                isPlaying = !isPlaying
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Пауза" else "Воспроизведение"
                )
            }

            IconButton(onClick = { /* onNext() */ }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Следующий трек")
            }
        }
    }
}


fun saveFileFromUri(context: Context, uri: Uri, music: Music?): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val outputFile = File(context.getExternalFilesDir(null), "${music?.name?.replace(" ", "_")}.mp3")
            FileOutputStream(outputFile).use { outputStream ->
                stream.copyTo(outputStream)
            }
            outputFile
        }
    } catch (e: Exception) {
        Log.e("SaveFile", "An error occurred while saving the file", e)
        null
    }
}