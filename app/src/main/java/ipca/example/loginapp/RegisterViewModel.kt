package ipca.example.loginapp

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun registerUser(onSuccess: () -> Unit) {
        errorMessage = ""

        // Validações básicas
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Preencha todos os campos"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Passwords não coincidem"
            return
        }
        if (password.length < 6) {
            errorMessage = "Password deve ter pelo menos 6 caracteres"
            return
        }

        isLoading = true

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Erro desconhecido"
                }
            }
    }
}

