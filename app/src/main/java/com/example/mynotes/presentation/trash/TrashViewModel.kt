package com.example.mynotes.presentation.trash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.GetDeletedNotesUseCase
import com.example.mynotes.domain.usecase.RestoreNoteUseCase
import com.example.mynotes.util.Constants.REFS_NOTES
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val restoreNoteUseCase: RestoreNoteUseCase,
    private val getDeletedNotesUseCase: GetDeletedNotesUseCase
) : ViewModel() {

    private val _deletedNotes = MutableStateFlow<List<Note>>(emptyList())
    val deletedNotes: StateFlow<List<Note>> = _deletedNotes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            getDeletedNotesUseCase().collect { result ->
                result.onSuccess { _deletedNotes.value = it }
                result.onFailure { Log.e("TrashViewModel", "Notes error: ${it.message}") }
            }
        }
    }

    fun restoreNote(note: Note) {
        viewModelScope.launch {
            val result = restoreNoteUseCase(note)
            result.onFailure {
                Log.e("HomeViewModel", "Soft delete failed: ${it.message}")
            }
        }
    }

    fun permanentlyDeleteNote(noteId: String?) {
        viewModelScope.launch {
            noteId?.let { id ->
                try {
                    db.getReference(REFS_NOTES).child(id)
                        .removeValue()
                        .addOnSuccessListener {
                            Log.d("TrashViewModel", "Note permanently deleted: $id")

                            // Local state'i hemen güncelle
                            val currentNotes = _deletedNotes.value.toMutableList()
                            currentNotes.removeAll { it.id == id }
                            _deletedNotes.value = currentNotes
                        }
                        .addOnFailureListener { exception ->
                            Log.e("TrashViewModel", "Permanent delete failed: ${exception.message}")
                        }
                } catch (e: Exception) {
                    Log.e("Firebase", "Not kalıcı silme hatası: ${e.message}")
                }
            }
        }
    }

    fun clearAllTrash() {
        viewModelScope.launch {
            try {
                val deletedNoteIds = _deletedNotes.value.mapNotNull { it.id }

                deletedNoteIds.forEach { id ->
                    db.getReference(REFS_NOTES).child(id).removeValue()
                }

                // Local state'i temizle
                _deletedNotes.value = emptyList()

            } catch (e: Exception) {
                Log.e("Firebase", "Çöp kutusu temizleme hatası: ${e.message}")
            }
        }
    }
}