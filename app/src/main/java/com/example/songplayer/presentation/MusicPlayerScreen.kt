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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.example.songplayer.domain.Music
import kotlinx.coroutines.delay
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

    LaunchedEffect(music) {
        mediaPlayer.reset()


        if (music?.musicLink != null) {
            val musicUri = Uri.parse(music.musicLink.toString())

            tempFile = saveFileFromUri(context, musicUri,)


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

fun saveFileFromUri(context: Context, uri: Uri): File? {
    try {
        // Получаем InputStream из URI
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        // Проверяем, что InputStream не нулевой
        if (inputStream != null) {
            // Определите, куда вы хотите сохранить новый файл
            val outputFile = File(context.getExternalFilesDir(null), "outputFileName.extension") // Укажите нужные вам имя и расширение

            // Используем FileOutputStream для записи данных
            FileOutputStream(outputFile).use { outputStream ->
                inputStream.copyTo(outputStream) // Копируем данные из inputStream в outputStream
            }

            // Закрываем inputStream после использования
            inputStream.close()
            return outputFile
        } else {
            Log.e("SaveFile", "Unable to open InputStream")
        }
    } catch (e: Exception) {
        Log.e("SaveFile", "An error occurred while saving the file", e)
    }
    return null
}