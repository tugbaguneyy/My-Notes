package com.example.mynotes.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface IFirebaseAuth {
    fun signInWithEmailAndPassword(email: String, password: String) : Flow<Boolean>
    fun signUpWithEmailAndPassword(email: String, password: String) : Flow<Boolean>
    suspend fun signOut()
    fun currentUser() : Flow<FirebaseUser?>
}