package ipca.example.loginapp.ui.songs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddSongView(
    navController: NavController,
    playlistId: String,
    viewModel: SongsViewModel = viewModel()
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var artist by remember { mutableStateOf(TextFieldValue("")) }
    var genre by remember { mutableStateOf(TextFieldValue("")) }

    val uiState by viewModel.uiState

    LaunchedEffect(playlistId) {
        viewModel.playlistId = playlistId
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Adicionar Nova Música",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da música") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = artist,
                onValueChange = { artist = it },
                label = { Text("Artista") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Género") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addSong(
                        title = title.text,
                        artist = artist.text,
                        genre = genre.text
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar Música")
            }

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Erro: $error", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
