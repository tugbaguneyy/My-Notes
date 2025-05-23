package com.example.mynotes.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.GetAllNotesUseCase
import com.example.mynotes.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
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

    fun toggleFavorite(note: Note, isFavorite: Boolean) {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(note, isFavorite)
            result.onFailure {
                Log.e("HomeViewModel", "Toggle favorite failed: ${it.message}")
            }
        }
    }
}