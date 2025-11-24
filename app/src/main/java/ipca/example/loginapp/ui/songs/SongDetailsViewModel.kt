package ipca.example.loginapp.ui.songs

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.loginapp.models.Song
import ipca.example.loginapp.repository.ResultWrapper
import ipca.example.loginapp.repository.SongRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongDetailViewState(
    var title: String? = null,
    var artist: String? = null,
    var genre: String? = null,
    var error: String? = null,
    var isLoading: Boolean = false
)

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    var uiState = mutableStateOf(SongDetailViewState())
        private set

    fun updateTitle(title: String) {
        uiState.value = uiState.value.copy(title = title)
    }

    fun updateArtist(artist: String) {
        uiState.value = uiState.value.copy(artist = artist)
    }

    fun updateGenre(genre: String) {
        uiState.value = uiState.value.copy(genre = genre)
    }

    // -------------------------------------------
    // FETCH SONG (SUSPEND)
    // -------------------------------------------
    fun fetchSong(songId: String, playlistId: String) {
        uiState.value = uiState.value.copy(isLoading = true)
        repository.getSong(playlistId, songId) { song ->
            if (song != null) {
                uiState.value = uiState.value.copy(
                    title = song.title,
                    artist = song.artist,
                    genre = song.genre,
                    isLoading = false,
                    error = null
                )
            } else {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = "Música não encontrada"
                )
            }
        }
    }

    // -------------------------------------------
    // SAVE SONG (ADD OU UPDATE) - SUSPEND
    // -------------------------------------------
    fun saveSong(songId: String? = null, playlistId: String) {
        uiState.value = uiState.value.copy(isLoading = true)

        val song = Song(
            docId = songId,
            title = uiState.value.title,
            artist = uiState.value.artist,
            genre = uiState.value.genre
        )

        repository.saveSong(song, playlistId) { success ->
            uiState.value = uiState.value.copy(
                isLoading = false,
                error = if (success) null else "Erro ao guardar a música"
            )
        }
    }



    // -------------------------------------------
    // DELETE SONG
    // -------------------------------------------
    fun deleteSong(
        songId: String,
        playlistId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            repository.deleteSong(playlistId, songId).collect { result ->
                when(result) {
                    is ResultWrapper.Success -> onSuccess()
                    is ResultWrapper.Error -> onFailure(result.message ?: "Erro ao eliminar música")
                    else -> {}
                }
            }
        }
    }
}
