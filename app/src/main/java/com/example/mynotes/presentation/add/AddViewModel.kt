package com.example.mynotes.presentation.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.usecase.AddNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    fun addNote(title: String, description: String, date: Date) {
        viewModelScope.launch {
            addNoteUseCase(title, description, date).collect { result ->
                result.onSuccess {
                    // success - maybe show a toast or navigate back
                    Log.d("AddNote", "Note added successfully")
                }.onFailure {
                    // handle error
                    Log.e("AddNote", "Error adding note: ${it.message}")
                }
            }
        }
    }
}
