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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ipca.example.loginapp.ui.home.HomeView
import ipca.example.loginapp.ui.login.UtilizadorLoginView
import ipca.example.loginapp.ui.profile.UserProfileView
import ipca.example.loginapp.ui.register.RegisterView
import ipca.example.loginapp.ui.songs.AddSongView
import ipca.example.loginapp.ui.songs.SongDetailView
import ipca.example.loginapp.ui.songs.SongsView
import ipca.example.loginapp.ui.theme.LoginAppTheme
import androidx.compose.material3.Text
import android.net.Uri


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

                        // Songs List Screen
                        composable(
                            route = "songs/{playlistId}",
                            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                            SongsView(navController = navController, playlistId = playlistId)
                        }

                        // Add Song Screen
                        composable(
                            route = "add_song/{playlistId}",
                            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                            AddSongView(navController = navController, playlistId = playlistId)
                        }

                        composable("song_detail/{songId}/{playlistId}") { backStackEntry ->
                            val songId = backStackEntry.arguments?.getString("songId")?.let { Uri.decode(it) }
                            val playlistId = backStackEntry.arguments?.getString("playlistId")?.let { Uri.decode(it) }

                            if (!songId.isNullOrEmpty() && !playlistId.isNullOrEmpty()) {
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
}
