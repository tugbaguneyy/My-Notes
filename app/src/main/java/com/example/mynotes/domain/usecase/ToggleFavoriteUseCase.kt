package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.model.Note
import com.example.mynotes.util.Constants
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(note: Note, isFavorite: Boolean): Result<Unit> {
        return try {
            val userId = currentUserUseCase().firstOrNull()?.uid ?: return Result.failure(Exception("User not authenticated"))
            val noteId = note.id ?: return Result.failure(Exception("Note ID is null"))

            suspendCancellableCoroutine { continuation ->
                db.getReference(Constants.REFS_NOTES)
                    .child(noteId)
                    .child("favorite")
                    .setValue(isFavorite)
                    .addOnSuccessListener {
                        continuation.resume(Result.success(Unit)) {}
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(Result.failure(e)) {}
                    }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
