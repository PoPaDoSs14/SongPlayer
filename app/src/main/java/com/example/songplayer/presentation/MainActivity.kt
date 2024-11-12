package com.example.songplayer.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.songplayer.ui.theme.SongPlayerTheme

class MainActivity : ComponentActivity() {

    private val PICK_AUDIO_REQUEST = 1
    private lateinit var playlistViewModel: PlaylistViewModel


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

            } else {

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                SongApp(navController = navController, playlistViewModel = playlistViewModel)
            }
        }
    }
}


