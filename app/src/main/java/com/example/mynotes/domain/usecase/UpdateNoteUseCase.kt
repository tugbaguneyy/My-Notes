package com.example.mynotes.domain.usecase

import com.example.mynotes.util.Constants.REFS_NOTES
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class UpdateNoteUseCase @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(noteId: String, title: String, description: String): Result<Unit> {
        return try {
            val userId = currentUserUseCase().first()?.uid ?: return Result.failure(Exception("User not authenticated"))

            val updates = mapOf(
                "title" to title,
                "description" to description,
                "updatedAt" to System.currentTimeMillis()
            )

            suspendCancellableCoroutine<Result<Unit>> { cont ->
                db.getReference(REFS_NOTES).child(noteId)
                    .updateChildren(updates)
                    .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                    .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
