package com.example.mynotes.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.LoadNoteUseCase
import com.example.mynotes.domain.usecase.SoftDeleteNoteUseCase
import com.example.mynotes.domain.usecase.ToggleFavoriteUseCase
import com.example.mynotes.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val softDeleteNoteUseCase: SoftDeleteNoteUseCase,
    private val loadNoteUseCase: LoadNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
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
            val result = updateNoteUseCase(noteId, title, description)
            result.onSuccess {
                Log.d("DetailViewModel", "Note updated successfully")
            }.onFailure { e ->
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