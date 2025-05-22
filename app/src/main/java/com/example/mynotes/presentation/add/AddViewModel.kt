package com.example.mynotes.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.util.Constants
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val db: FirebaseDatabase,
    private val currentUserUseCase: CurrentUserUseCase
) : ViewModel() {

    fun addNote(
        title: String,
        description: String,
        date: Date
    ) {
        viewModelScope.launch {
            val userId = currentUserUseCase().first()?.uid ?: return@launch
            val ref = db.reference.child(Constants.REFS_NOTES).push()
            val id = ref.key ?: return@launch

            val note = Note(
                id = id,
                userId = userId,
                title = title,
                description = description,
                date = date,
                isFavorite = false,
                isDeleted = false
            )

            ref.setValue(note)
        }
    }
}
