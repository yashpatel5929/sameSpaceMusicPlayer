package com.samespace.musicapp.presentation.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samespace.musicapp.data.model.SongList
import com.samespace.musicapp.presentation.compose.homescreen.HomeScreen
import com.samespace.musicapp.presentation.compose.musicplayerscreen.MusicPlayer
import com.samespace.musicapp.presentation.viewmodels.HomeViewModel


@Composable
fun MusicAppNavigation() {
    val navController = rememberNavController()
    MusicAppNavHost(
        navController = navController
    )
}

@Composable
fun MusicAppNavHost(navController : NavHostController){
    val activity = (LocalContext.current as Activity)
    val homeViewModel: HomeViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                onSongItemClick =  { Id , songList ->
                    navController.navigate("musicDetail/${Id}")
            })
        }
        composable(
            route = "musicDetail/{Id}",
            arguments = listOf(navArgument("Id") { type = NavType.IntType })
        ){
            MusicPlayer(navController = navController , homeViewModel )
        }
    }
}