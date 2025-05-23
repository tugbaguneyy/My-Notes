package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.model.Note
import com.example.mynotes.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetDeletedNotesUseCase @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) {
    operator fun invoke(): Flow<Result<List<Note>>> = callbackFlow {
        val userId = currentUserUseCase().firstOrNull()?.uid
        if (userId == null) {
            trySend(Result.failure(Exception("User not authenticated")))
            close()
            return@callbackFlow
        }

        val notesRef = db.getReference(Constants.REFS_NOTES)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = mutableListOf<Note>()
                for (data in snapshot.children) {
                    val note = data.getValue(Note::class.java)
                    if (note != null && note.userId == userId && note.isDeleted) {
                        notes.add(note.copy(id = data.key))
                    }
                }
                trySend(Result.success(notes))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(Exception(error.message)))
            }
        }

        notesRef.addValueEventListener(listener)

        awaitClose {
            notesRef.removeEventListener(listener)
        }
    }.flowOn(Dispatchers.IO)
}