package ipca.example.loginapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

data class SongDetailViewState(
    var title: String? = null,
    var artist: String? = null,
    var genre: String? = null,
    var error: String? = null,
    var isLoading: Boolean = false
)

class SongDetailViewModel : ViewModel() {

    var uiState = mutableStateOf(SongDetailViewState())
        private set

    private val db = Firebase.firestore

    fun updateTitle(title: String) {
        uiState.value = uiState.value.copy(title = title)
    }

    fun updateArtist(artist: String) {
        uiState.value = uiState.value.copy(artist = artist)
    }

    fun updateGenre(genre: String) {
        uiState.value = uiState.value.copy(genre = genre)
    }

    fun fetchSong(songId: String, playlistId: String) {
        uiState.value = uiState.value.copy(isLoading = true)
        val docRef = db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .document(songId)

        docRef.get()
            .addOnSuccessListener { doc ->
                val song = doc.toObject(Song::class.java)
                if (song != null) {
                    uiState.value = uiState.value.copy(
                        title = song.title,
                        artist = song.artist,
                        genre = song.genre,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
    }

    fun saveSong(playlistId: String, songId: String? = null) {
        uiState.value = uiState.value.copy(isLoading = true)

        val song = Song(
            title = uiState.value.title,
            artist = uiState.value.artist,
            genre = uiState.value.genre
        )

        val collectionRef = db.collection("playlists")
            .document(playlistId)
            .collection("songs")

        if (songId == null) {
            collectionRef.add(song)
                .addOnSuccessListener { docRef ->
                    Log.d("SongDetailViewModel", "Song added with ID: ${docRef.id}")
                    uiState.value = uiState.value.copy(isLoading = false)
                }
                .addOnFailureListener { e ->
                    Log.w("SongDetailViewModel", "Error adding song", e)
                    uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage)
                }
        } else {
            collectionRef.document(songId)
                .set(song)
                .addOnSuccessListener {
                    Log.d("SongDetailViewModel", "Song updated with ID: $songId")
                    uiState.value = uiState.value.copy(isLoading = false)
                }
                .addOnFailureListener { e ->
                    Log.w("SongDetailViewModel", "Error updating song", e)
                    uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage)
                }
        }
    }

    fun deleteSong(playlistId: String, songId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .document(songId)
            .delete()
            .addOnSuccessListener {
                Log.d("SongDetailViewModel", "Song deleted with ID: $songId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w("SongDetailViewModel", "Error deleting song", e)
                onFailure(e.localizedMessage ?: "Erro ao eliminar música")
            }
    }
}
