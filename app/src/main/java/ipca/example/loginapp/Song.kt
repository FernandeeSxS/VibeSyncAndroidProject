package ipca.example.loginapp

data class Song(
    var docId: String? = null,      // ID do documento no Firestore
    var title: String? = null,      // Nome da música
    var artist: String? = null,     // Artista
    var genre: String? = null,      // Género musical
    var rating: Int? = null         // Classificação (ex: 1–5 estrelas)
)
