package ipca.example.loginapp

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

data class UserProfileState(
    val email: String = "",
    val error: String? = null,
    val isLoading: Boolean = false
)

class UserProfileViewModel : ViewModel() {

    var uiState by mutableStateOf(UserProfileState())
        private set

    private val auth = FirebaseAuth.getInstance()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        val user = auth.currentUser
        if (user == null) {
            uiState = uiState.copy(error = "Utilizador não está logado")
            return
        }

        uiState = uiState.copy(
            email = user.email ?: "",
            isLoading = false,
            error = null
        )
    }

    fun updateEmail(newEmail: String, currentPassword: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser ?: return

        uiState = uiState.copy(isLoading = true)

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updateEmail(newEmail).addOnCompleteListener { updateTask ->
                    uiState = uiState.copy(isLoading = false)
                    if (updateTask.isSuccessful) {
                        // Logout imediatamente após alterar o email
                        auth.signOut()
                        onComplete(true, null) // indica que deve voltar ao login
                    } else {
                        onComplete(false, updateTask.exception?.localizedMessage)
                    }
                }
            } else {
                uiState = uiState.copy(isLoading = false)
                onComplete(false, reauthTask.exception?.localizedMessage)
            }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser ?: return

        uiState = uiState.copy(isLoading = true)

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    uiState = uiState.copy(isLoading = false)
                    if (updateTask.isSuccessful) {
                        onComplete(true, null)
                    } else {
                        onComplete(false, updateTask.exception?.localizedMessage)
                    }
                }
            } else {
                uiState = uiState.copy(isLoading = false)
                onComplete(false, reauthTask.exception?.localizedMessage)
            }
        }
    }

    fun setError(message: String?) {
        uiState = uiState.copy(error = message)
    }
}
