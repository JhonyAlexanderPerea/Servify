package co.edu.uniquindio.servify.data.remote.user

import co.edu.uniquindio.servify.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// El id no es un campo confiable del documento sino su nombre, por eso se asigna aparte con id
fun DocumentSnapshot.toUser(): User? =
    toObject(UserDto::class.java)?.copy(id = id)?.toDomain()

// Escucha el documento y emite el usuario (null si no existe). Patrón callbackFlow + awaitClose
// de la actividad práctica 3 de la guía de Firestore: el listener solo vive mientras hay colectores.
// Un error del listener (p. ej. permisos al cerrar sesión) termina el flujo sin propagar la excepción.
fun DocumentReference.observeUser(): Flow<User?> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close()
            return@addSnapshotListener
        }
        trySend(snapshot?.toUser())
    }
    awaitClose { registration.remove() }
}

// Emite el usuario de Firebase Authentication cada vez que cambia el estado de autenticación
fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth -> trySend(firebaseAuth.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}
