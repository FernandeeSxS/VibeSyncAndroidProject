package ipca.example.loginapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ipca.example.loginapp.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlaylistRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    fun fetchPlaylists(): Flow<ResultWrapper<List<Playlist>>> = flow {
        try {
            emit(ResultWrapper.Loading())

            val userId = getCurrentUserId() ?: throw Exception("Utilizador não logado")

            val query = db
                .collection("playlists")
                .whereEqualTo("ownerId", userId)

            query.snapshotFlow().collect { snapshot ->
                val playlists = mutableListOf<Playlist>()
                for (doc in snapshot.documents) {
                    val playlist = doc.toObject(Playlist::class.java)
                    playlist?.docId = doc.id
                    if (playlist != null) playlists.add(playlist)
                }

                emit(ResultWrapper.Success(playlists.toList()))
            }

        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.localizedMessage ?: "Unexpected error"))
        }
    }.flowOn(Dispatchers.IO)

    fun createPlaylist(playlist: Playlist): Flow<ResultWrapper<Unit>> = flow {
        try {
            emit(ResultWrapper.Loading())

            db.collection("playlists")
                .add(playlist)
                .await()

            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.localizedMessage ?: "Unexpected error"))
        }
    }.flowOn(Dispatchers.IO)
}

