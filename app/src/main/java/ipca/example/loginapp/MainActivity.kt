package ipca.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ipca.example.loginapp.ui.theme.LoginAppTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ipca.example.loginapp.ui.home.HomeView
import ipca.example.loginapp.ui.login.UtilizadorLoginView
import ipca.example.loginapp.ui.profile.UserProfileView
import ipca.example.loginapp.ui.register.RegisterView
import ipca.example.loginapp.ui.songs.AddSongView
import ipca.example.loginapp.ui.songs.SongDetailView
import ipca.example.loginapp.ui.songs.SongsView
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "LoginApp"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            LoginAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Login Screen
                        composable("login") {
                            UtilizadorLoginView(navController = navController)
                        }

                        // Register Screen
                        composable("register") {
                            RegisterView(navController = navController)
                        }

                        // Home Screen
                        composable("home") {
                            HomeView(navController = navController)
                        }

                        // Profile Screen
                        composable("profile") {
                            UserProfileView(navController = navController)
                        }

                        // Rota para a Lista de Músicas (SongsView)
                        composable(

                            route = "songs/{playlistId}",


                            arguments = listOf(
                                navArgument("playlistId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->


                            val playlistId = backStackEntry.arguments?.getString("playlistId")

                            playlistId?.let {
                                SongsView(navController = navController, playlistId = it)
                            }
                        }

                        composable("add_song/{playlistId}") { backStackEntry ->
                            val playlistId = backStackEntry.arguments?.getString("playlistId")!!
                            AddSongView(
                                navController = navController,
                                playlistId = playlistId
                            )
                        }

                        composable("song_detail/{songId}/{playlistId}") { backStackEntry ->
                            val songId = backStackEntry.arguments?.getString("songId")!!
                            val playlistId = backStackEntry.arguments?.getString("playlistId")!!
                            SongDetailView(
                                navController = navController,
                                songId = songId,
                                playlistId = playlistId
                            )
                        }

                    }
                }
            }
        }
    }
}
