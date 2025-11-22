package ipca.example.loginapp.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.loginapp.models.Playlist
import ipca.example.loginapp.repository.PlaylistRepository
import ipca.example.loginapp.repository.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class HomeViewState(
    var playlists: List<Playlist> = emptyList(),
    var error: String? = null,
    var isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    var uiState = mutableStateOf(HomeViewState())
        private set

    fun fetchPlaylists() {
        playlistRepository.fetchPlaylists().onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        playlists = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true
                    )
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

    fun createPlaylist(name: String) {
        playlistRepository.createPlaylist(
            Playlist(
                name = name,
                ownerId = playlistRepository.getCurrentUserId()
            )
        ).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true
                    )
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
}
