package com.example.mynotes.data.remote.repository

import com.example.mynotes.domain.repository.IFirebaseAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseAuthImpl @Inject constructor(
    private val auth: FirebaseAuth
) : IFirebaseAuth {
    override fun signInWithEmailAndPassword(email: String, password: String): Flow<Boolean> = callbackFlow {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                trySend(task.isSuccessful).isSuccess // Veriyi yay
                close() // Akışı kapat
            }
            .addOnFailureListener {
                trySend(false).isSuccess // Hata durumunda false yay
                close()
            }
        awaitClose {

        }
    }

    override fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ) : Flow<Boolean> = callbackFlow  {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                trySend(task.isSuccessful).isSuccess // Veriyi yay
                close() // Akışı kapat
            }
            .addOnFailureListener {
                trySend(false).isSuccess // Hata durumunda false yay
                close()
            }
        awaitClose {

        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun currentUser(): Flow<FirebaseUser?> = flow{
        emit(auth.currentUser)
    }

}