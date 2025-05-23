package com.example.mynotes.presentation.trash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.ClearAllTrashUseCase
import com.example.mynotes.domain.usecase.GetDeletedNotesUseCase
import com.example.mynotes.domain.usecase.PermanentlyDeleteNoteUseCase
import com.example.mynotes.domain.usecase.RestoreNoteUseCase
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
    private val getDeletedNotesUseCase: GetDeletedNotesUseCase,
    private val permanentlyDeleteNoteUseCase: PermanentlyDeleteNoteUseCase,
    private val clearAllTrashUseCase: ClearAllTrashUseCase
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
                val result = permanentlyDeleteNoteUseCase(id)
                result.onSuccess {
                    val currentNotes = _deletedNotes.value.toMutableList()
                    currentNotes.removeAll { note -> note.id == id }
                    _deletedNotes.value = currentNotes
                    Log.d("TrashViewModel", "Note permanently deleted: $id")
                }.onFailure { e ->
                    Log.e("TrashViewModel", "Permanent delete failed: ${e.message}")
                }
            }
        }
    }


    fun clearAllTrash() {
        viewModelScope.launch {
            val noteIds = _deletedNotes.value.mapNotNull { it.id }
            val result = clearAllTrashUseCase(noteIds)
            result.onSuccess {
                _deletedNotes.value = emptyList()
                Log.d("TrashViewModel", "Trash cleared.")
            }.onFailure { e ->
                Log.e("TrashViewModel", "Trash clear failed: ${e.message}")
            }
        }
    }
}