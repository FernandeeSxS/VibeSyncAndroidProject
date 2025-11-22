package ipca.example.loginapp.ui.songs

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import ipca.example.loginapp.models.Song

data class SongsViewState(
    var songs: List<Song> = emptyList(),
    var error: String? = null,
    var isLoading: Boolean = false
)

class SongsViewModel : ViewModel() {

    var uiState = mutableStateOf(SongsViewState())
        private set

    var playlistId: String? = null

    private val db = Firebase.firestore

    fun fetchSongs(playlistId: String) {
        uiState.value = uiState.value.copy(isLoading = true)
        this.playlistId = playlistId

        val docRef = db
            .collection("playlists")
            .document(playlistId)
            .collection("songs")

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("SongsViewModel", "Listen failed.", e)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
                return@addSnapshotListener
            }

            val songs = mutableListOf<Song>()
            for (doc in snapshot?.documents ?: emptyList()) {
                val song = doc.toObject(Song::class.java)
                song?.docId = doc.id
                song?.let { songs.add(it) }
            }

            uiState.value = uiState.value.copy(
                songs = songs,
                isLoading = false,
                error = null
            )
        }
    }

    fun addSong(title: String, artist: String, genre: String) {
        if (playlistId == null) return

        uiState.value = uiState.value.copy(isLoading = true)

        val newSong = Song(
            title = title,
            artist = artist,
            genre = genre
        )

        db.collection("playlists")
            .document(playlistId!!)
            .collection("songs")
            .add(newSong)
            .addOnSuccessListener { docRef ->
                Log.d("SongsViewModel", "Song added with ID: ${docRef.id}")
                uiState.value = uiState.value.copy(isLoading = false)
            }
            .addOnFailureListener { e ->
                Log.w("SongsViewModel", "Error adding song", e)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
    }
}
