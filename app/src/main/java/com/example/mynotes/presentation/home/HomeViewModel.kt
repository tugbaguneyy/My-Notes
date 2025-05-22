package com.example.mynotes.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.CurrentUserUseCase
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
    private val signOutUseCase: SignOutUseCase,
    private val currentUserUseCase: CurrentUserUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean>
        get() = _isAuthenticated.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>>
        get() = _notes.asStateFlow()

    init {
        isUserAuthenticated()
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            val userId = currentUserUseCase().first()?.uid ?: return@launch

            db.getReference(REFS_NOTES).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val notes = mutableListOf<Note>()

                    for (data in snapshot.children) {

                        val note = data.getValue(Note::class.java)

                        if (note != null && note.userId == userId) {
                            notes.add(note)
                        }
                    }
                    _notes.value = notes

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Veri çekme iptal edildi: ${error.message}")
                }
            })
        }
    }


    fun signOut(){
        viewModelScope.launch {
            signOutUseCase()
            _isAuthenticated.value = false
        }
    }

    private fun isUserAuthenticated() {
        viewModelScope.launch {
            val isActive = currentUserUseCase().first() != null
            _isAuthenticated.value = isActive
        }
    }
}