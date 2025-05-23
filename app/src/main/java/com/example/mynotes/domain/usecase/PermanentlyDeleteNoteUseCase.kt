package com.example.mynotes.domain.usecase

import com.example.mynotes.util.Constants
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class PermanentlyDeleteNoteUseCase @Inject constructor(
    private val db: FirebaseDatabase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(noteId: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            db.getReference(Constants.REFS_NOTES)
                .child(noteId)
                .removeValue()
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit)) {}
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception)) {}
                }
        }
    }
}
