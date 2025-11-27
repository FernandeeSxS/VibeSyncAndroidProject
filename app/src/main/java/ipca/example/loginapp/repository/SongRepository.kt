package ipca.example.loginapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import ipca.example.loginapp.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun fetchSongs(playlistId: String): Flow<ResultWrapper<List<Song>>> = flow {
        try {
            emit(ResultWrapper.Loading())

            val query = db.collection("playlists")
                .document(playlistId)
                .collection("songs")

            query.snapshotFlow().collect { snapshot ->
                val songs = mutableListOf<Song>()
                for (doc in snapshot.documents) {
                    val song = doc.toObject(Song::class.java)
                    song?.docId = doc.id
                    song?.let { songs.add(it) }
                }

                emit(ResultWrapper.Success(songs.toList()))
            }

        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.localizedMessage ?: "Unexpected error"))
        }
    }.flowOn(Dispatchers.IO)


    fun getSong(playlistId: String, songId: String, onResult: (Song?) -> Unit) {
        db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .document(songId)
            .get()
            .addOnSuccessListener { doc ->
                val song = doc.toObject(Song::class.java)?.apply { docId = doc.id }
                onResult(song)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    fun addSong(playlistId: String, song: Song, onResult: (Boolean) -> Unit) {
        db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .add(song)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateSong(playlistId: String, song: Song, onResult: (Boolean) -> Unit) {
        if (song.docId == null) {
            onResult(false)
            return
        }
        db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .document(song.docId!!)
            .set(song)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun saveSong(song: Song, playlistId: String, onResult: (Boolean) -> Unit) {
        if (song.docId == null) {
            addSong(playlistId, song, onResult)
        } else {
            updateSong(playlistId, song, onResult)
        }
    }


    fun deleteSong(playlistId: String, songId: String): Flow<ResultWrapper<Unit>> = flow {
        try {
            emit(ResultWrapper.Loading())

            db.collection("playlists")
                .document(playlistId)
                .collection("songs")
                .document(songId)
                .delete()
                .await()

            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.localizedMessage ?: "Erro ao eliminar música"))
        }
    }.flowOn(Dispatchers.IO)
}

