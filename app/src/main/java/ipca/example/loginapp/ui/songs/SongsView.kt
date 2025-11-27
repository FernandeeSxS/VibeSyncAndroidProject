package ipca.example.loginapp.ui.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.example.loginapp.ui.theme.LoginAppTheme

@Composable
fun SongsView(
    navController: NavController,
    playlistId: String,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(playlistId) {
        viewModel.fetchSongs(playlistId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🎶 Músicas da Playlist",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.songs.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sem músicas 🎵",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(uiState.songs) { _, song ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        song.docId?.let { songId ->
                                            navController.navigate("song_detail/$songId/$playlistId")
                                        }
                                    }
                                    .padding(vertical = 14.dp, horizontal = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🎵",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = song.title ?: "Sem título",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "Artista: ${song.artist ?: "Desconhecido"}",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Divider(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .height(1.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("add_song/$playlistId") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Adicionar Música")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongsViewPreview() {
    LoginAppTheme {
        SongsView(
            navController = rememberNavController(),
            playlistId = "123"
        )
    }
}
