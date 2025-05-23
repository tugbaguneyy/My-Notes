package com.example.mynotes.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.LoadNoteUseCase
import com.example.mynotes.domain.usecase.SoftDeleteNoteUseCase
import com.example.mynotes.domain.usecase.ToggleFavoriteUseCase
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
    private val currentUserUseCase: CurrentUserUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val softDeleteNoteUseCase: SoftDeleteNoteUseCase,
    private val loadNoteUseCase: LoadNoteUseCase
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            loadNoteUseCase(noteId).collect { result ->
                result.onSuccess { _note.value = it }
                    .onFailure { e -> Log.e("DetailViewModel", "Load note error: ${e.message}") }
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
            val result = toggleFavoriteUseCase(note, isFavorite)
            result.onFailure {
                Log.e("HomeViewModel", "Toggle favorite failed: ${it.message}")
            }
        }
    }

    fun softDeleteNote(note: Note) {
        viewModelScope.launch {
            val result = softDeleteNoteUseCase(note)
            result.onFailure {
                Log.e("HomeViewModel", "Soft delete failed: ${it.message}")
            }
        }
    }
}