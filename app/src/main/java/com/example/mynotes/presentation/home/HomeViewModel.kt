package com.example.mynotes.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.GetAllNotesUseCase
import com.example.mynotes.domain.usecase.SignOutUseCase
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
class HomeViewModel @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean>
        get() = _isAuthenticated.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>>
        get() = _notes.asStateFlow()

    init {
        isUserAuthenticated()
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect { result ->
                result.onSuccess { _notes.value = it }
                result.onFailure { Log.e("HomeViewModel", "Notes error: ${it.message}") }
            }
        }
    }

    private fun isUserAuthenticated() {
        viewModelScope.launch {
            val isActive = currentUserUseCase().first() != null
            _isAuthenticated.value = isActive
        }
    }

    // Favorite işlemi için yeni fonksiyon
    fun toggleFavorite(note: Note, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                val userId = currentUserUseCase().first()?.uid ?: return@launch

                // Note'un id'si varsa Firebase'de güncelle
                note.id?.let { noteId ->
                    val noteRef = db.getReference(REFS_NOTES).child(noteId)

                    // Sadece isFavorite field'ını güncelle
                    noteRef.child("favorite").setValue(isFavorite)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Favorite durumu güncellendi: $isFavorite")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Favorite güncelleme hatası: ${exception.message}")
                        }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Toggle favorite error: ${e.message}")
            }
        }
    }
}