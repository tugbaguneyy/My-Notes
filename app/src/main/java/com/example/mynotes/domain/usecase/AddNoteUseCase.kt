package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.model.Note
import com.example.mynotes.util.Constants
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class AddNoteUseCase @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) {

    operator fun invoke(
        title: String,
        description: String,
        date: Date
    ): Flow<Result<Unit>> = flow {
        try {
            val userId = currentUserUseCase().first()?.uid ?: throw Exception("User not logged in")
            val ref = db.reference.child(Constants.REFS_NOTES).push()
            val id = ref.key ?: throw Exception("Could not generate ID")

            val note = Note(
                id = id,
                userId = userId,
                title = title,
                description = description,
                date = date,
                isFavorite = false,
                isDeleted = false
            )

            // Firebase işlemi suspend fonksiyona çevrilmiş hali
            suspendCancellableCoroutine<Unit> { continuation ->
                ref.setValue(note)
                    .addOnSuccessListener {
                        continuation.resume(Unit) {} // işlem başarıyla tamamlandı
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e) // hata fırlat
                    }
            }

            emit(Result.success(Unit))

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
