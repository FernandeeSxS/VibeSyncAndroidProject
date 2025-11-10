package ipca.example.loginapp

data class Playlist(
    var docId: String? = null,          // id do documento no Firestore
    var name: String? = null,           // nome da playlist
    var ownerId: String? = null         // id do utilizador dono da playlist
)
