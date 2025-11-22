package ipca.example.loginapp.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.MusicNote

import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileView(navController: NavController, viewModel: UserProfileViewModel = viewModel()) {
    val uiState = viewModel.uiState

    var showEmailDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var currentPasswordForEmail by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Avatar musical",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Perfil do VibeSync",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mantém a tua vibe sempre sincronizada! 🎵",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Email: ${uiState.email}")
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Email",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            newEmail = uiState.email
                            showEmailDialog = true
                        }
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Password: ******")
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Password",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showPasswordDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Sair/Trocar de Conta 🎶", color = MaterialTheme.colorScheme.onSecondary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

