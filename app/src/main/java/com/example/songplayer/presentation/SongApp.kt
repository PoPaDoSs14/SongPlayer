package com.example.songplayer.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SongApp(navController: NavHostController) {


    NavHost(
        navController = navController,
        startDestination = "PlaylistScreen")
    {
        composable("PlaylistScreen") { PlaylistScreen(musicList = emptyList()) }
    }
}