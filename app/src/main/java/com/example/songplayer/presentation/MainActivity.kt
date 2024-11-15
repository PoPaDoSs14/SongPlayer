package com.example.songplayer.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.songplayer.ui.theme.SongPlayerTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val PICK_AUDIO_REQUEST = 1
    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var getContent: ActivityResultLauncher<Intent>


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

            } else {

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let { saveFileFromUri(this, it) }
            }
        }

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже предоставлено
            }
            else -> {
                // Запрашиваем разрешение
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        enableEdgeToEdge()
        playlistViewModel = PlaylistViewModel(application)
        setContent {
            SongPlayerTheme {
                val navController = rememberNavController()
                SongApp(navController = navController, playlistViewModel = playlistViewModel, context = application, getContent = getContent)
            }
        }
    }


    private fun saveFileFromUri(context: Context, uri: Uri): File? {
        // Ваша функция для сохранения файла
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val outputFile = File(context.getExternalFilesDir(null), "song.mp3")
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
}


