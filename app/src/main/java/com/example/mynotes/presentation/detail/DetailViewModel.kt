package com.example.mynotes.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.util.Constants.REFS_NOTES
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserUseCase().first()?.uid ?: return@launch

                db.getReference(REFS_NOTES).child(noteId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val note = snapshot.getValue(Note::class.java)
                            if (note != null && note.userId == userId) {
                                // Firebase'den gelen note objesine id'yi manuel olarak set ediyoruz
                                _note.value = note.copy(id = noteId)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Note yükleme hatası: ${error.message}")
                        }
                    })
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Load note error: ${e.message}")
            }
        }
    }

    fun updateNote(noteId: String, title: String, description: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserUseCase().first()?.uid ?: return@launch

                val updates = mapOf(
                    "title" to title,
                    "description" to description,
                    "updatedAt" to System.currentTimeMillis()
                )

                db.getReference(REFS_NOTES).child(noteId)
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Note güncellendi")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firebase", "Note güncelleme hatası: ${exception.message}")
                    }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Update note error: ${e.message}")
            }
        }
    }

    fun toggleFavorite(note: Note, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                val userId = currentUserUseCase().first()?.uid ?: return@launch

                note.id?.let { noteId ->
                    val noteRef = db.getReference(REFS_NOTES).child(noteId)

                    noteRef.child("favorite").setValue(isFavorite)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Favorite durumu güncellendi: $isFavorite")
                            // Local state'i de güncelle
                            _note.value = _note.value?.copy(isFavorite = isFavorite)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Favorite güncelleme hatası: ${exception.message}")
                        }
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Toggle favorite error: ${e.message}")
            }
        }
    }

    fun softDeleteNote(note: Note, isDeleted: Boolean) {
        viewModelScope.launch {
            try {
                val userId = currentUserUseCase().first()?.uid ?: return@launch

                note.id?.let { noteId ->
                    val noteRef = db.getReference(REFS_NOTES).child(noteId)

                    noteRef.child("deleted").setValue(isDeleted)
                        .addOnSuccessListener {
                            Log.d("Firebase", "isDeleted durumu güncellendi: $isDeleted")
                            // Local state'i de güncelle
                            _note.value = _note.value?.copy(isFavorite = isDeleted)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Soft delete güncelleme hatası: ${exception.message}")
                        }
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Soft delete error: ${e.message}")
            }
        }
    }
}