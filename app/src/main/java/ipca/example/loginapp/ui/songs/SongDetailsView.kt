package ipca.example.loginapp.ui.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SongDetailView(
    navController: NavController,
    playlistId: String,
    songId: String,
    viewModel: SongDetailViewModel = hiltViewModel<SongDetailViewModel>()
) {
    val uiState by viewModel.uiState

    var showEditDialog by remember { mutableStateOf(false) }
    var fieldToEdit by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // FETCH SONG
    LaunchedEffect(songId, playlistId) {
        if (songId.isNotEmpty() && playlistId.isNotEmpty()) {
            viewModel.fetchSong(songId, playlistId)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Detalhes da Música",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1FA2),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            @Composable
            fun InfoRow(label: String, value: String?, onEdit: () -> Unit) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .shadow(1.dp, CircleShape)
                        .background(Color(0xFFEDE7F6))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "$label:",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF512DA8),
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = value ?: "—",
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar $label",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onEdit() },
                        tint = Color(0xFF512DA8)
                    )
                }
            }

            InfoRow("Título", uiState.title) {
                fieldToEdit = "title"
                newValue = uiState.title ?: ""
                showEditDialog = true
            }

            InfoRow("Artista", uiState.artist) {
                fieldToEdit = "artist"
                newValue = uiState.artist ?: ""
                showEditDialog = true
            }

            InfoRow("Género", uiState.genre) {
                fieldToEdit = "genre"
                newValue = uiState.genre ?: ""
                showEditDialog = true
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Eliminar Música", color = Color.White, fontSize = 18.sp)
            }
        }

        // EDIT POPUP
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar ${fieldToEdit.replaceFirstChar { it.uppercase() }}") },
                text = {
                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = it },
                        label = { Text(fieldToEdit.replaceFirstChar { it.uppercase() }) },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        when (fieldToEdit) {
                            "title" -> viewModel.updateTitle(newValue)
                            "artist" -> viewModel.updateArtist(newValue)
                            "genre" -> viewModel.updateGenre(newValue)
                        }
                        viewModel.saveSong(songId, playlistId)
                        showEditDialog = false
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
                }
            )
        }

        // DELETE POPUP
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar Música") },
                text = { Text("Tens a certeza que queres eliminar esta música?") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        onClick = {
                            viewModel.deleteSong(
                                songId = songId,
                                playlistId = playlistId,
                                onSuccess = { navController.popBackStack() },
                                onFailure = { errorMsg -> println("Erro ao eliminar música: $errorMsg") }
                            )
                            showDeleteDialog = false
                        }
                    ) { Text("Eliminar", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
