package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.model.Note
import com.example.mynotes.util.Constants.REFS_NOTES
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadNoteUseCase @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) {
    operator fun invoke(noteId: String): Flow<Result<Note>> = callbackFlow {
        try {
            val userId = currentUserUseCase().first()?.uid
            if (userId == null) {
                trySend(Result.failure(Exception("User not authenticated")))
                close()
                return@callbackFlow
            }

            val noteRef = db.getReference(REFS_NOTES).child(noteId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val note = snapshot.getValue(Note::class.java)
                    if (note != null && note.userId == userId) {
                        trySend(Result.success(note.copy(id = noteId)))
                    } else {
                        trySend(Result.failure(Exception("Note not found or doesn't belong to user")))
                    }
                    close()
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(Exception(error.message)))
                    close()
                }
            }

            noteRef.addListenerForSingleValueEvent(listener)

            awaitClose {
                noteRef.removeEventListener(listener)
            }

        } catch (e: Exception) {
            trySend(Result.failure(e))
            close()
        }
    }
}
