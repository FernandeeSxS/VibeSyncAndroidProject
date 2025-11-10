package ipca.example.loginapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class HomeViewState(
    var playlists: List<Playlist> = emptyList(),
    var error: String? = null,
    var isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {

    var uiState = mutableStateOf(HomeViewState())
        private set

    private val db = FirebaseFirestore.getInstance()

    fun fetchPlaylists() {
        uiState.value = uiState.value.copy(isLoading = true)

        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID == null) {
            uiState.value = uiState.value.copy(
                isLoading = false,
                error = "Utilizador não está logado"
            )
            return
        }

        db.collection("playlists")
            .whereEqualTo("ownerId", userID)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeViewModel", "Erro ao ouvir Firestore", e)
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                    return@addSnapshotListener
                }

                val playlists = mutableListOf<Playlist>()
                for (doc in snapshot?.documents ?: emptyList()) {
                    val playlist = doc.toObject(Playlist::class.java)
                    playlist?.docId = doc.id
                    playlist?.let { playlists.add(it) }
                }

                uiState.value = uiState.value.copy(
                    playlists = playlists,
                    isLoading = false,
                    error = null
                )
            }
    }

    fun createPlaylist(name: String) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val playlist = Playlist(
            name = name,
            ownerId = userID
        )

        db.collection("playlists")
            .add(playlist)
            .addOnSuccessListener { docRef ->
                Log.d("HomeViewModel", "Playlist criada com ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.w("HomeViewModel", "Erro a criar playlist", e)
                uiState.value = uiState.value.copy(
                    error = e.localizedMessage
                )
            }
    }
}
