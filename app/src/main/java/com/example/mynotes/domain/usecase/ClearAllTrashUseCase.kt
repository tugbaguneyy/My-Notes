package com.example.mynotes.domain.usecase

import com.example.mynotes.util.Constants
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class ClearAllTrashUseCase @Inject constructor(
    private val db: FirebaseDatabase
) {
    suspend operator fun invoke(noteIds: List<String>): Result<Unit> {
        return try {
            noteIds.forEach { id ->
                db.getReference(Constants.REFS_NOTES).child(id).removeValue()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
