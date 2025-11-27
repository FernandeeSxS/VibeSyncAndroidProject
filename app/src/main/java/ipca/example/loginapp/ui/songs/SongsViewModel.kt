package ipca.example.loginapp.ui.songs

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.loginapp.models.Song
import ipca.example.loginapp.repository.ResultWrapper
import ipca.example.loginapp.repository.SongRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class SongsViewState(
    var songs: List<Song> = emptyList(),
    var error: String? = null,
    var isLoading: Boolean = false
)

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    var uiState = mutableStateOf(SongsViewState())
        private set

    var playlistId: String? = null

    fun fetchSongs(playlistId: String) {
        this.playlistId = playlistId
        uiState.value = uiState.value.copy(isLoading = true)

        repository.fetchSongs(playlistId).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        songs = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addSong(playlistId: String, title: String, artist: String, genre: String) {
        uiState.value = uiState.value.copy(isLoading = true)
        val song = Song(title = title, artist = artist, genre = genre)

        repository.saveSong(song, playlistId) { success ->
            uiState.value = uiState.value.copy(
                isLoading = false,
                error = if (success) null else "Erro ao guardar música"
            )
        }
    }
}
