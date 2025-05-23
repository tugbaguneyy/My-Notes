package com.example.mynotes.presentation.trash

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
class TrashViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ViewModel() {

    private val _deletedNotes = MutableStateFlow<List<Note>>(emptyList())
    val deletedNotes: StateFlow<List<Note>> = _deletedNotes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getDeletedNotes()
    }

    private fun getDeletedNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = currentUserUseCase().first()?.uid ?: return@launch

            db.getReference(REFS_NOTES).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notes = mutableListOf<Note>()

                    for (data in snapshot.children) {
                        val note = data.getValue(Note::class.java)

                        // userId kontrolü ve isDeleted true kontrolü
                        if (note != null &&
                            note.userId == userId &&
                            note.isDeleted
                        ) {
                            // Firebase'den gelen note'a id'yi manuel set et
                            val noteWithId = note.copy(id = data.key)
                            notes.add(noteWithId)
                        }
                    }
                    _deletedNotes.value = notes
                    _isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Silinmiş notlar çekme iptal edildi: ${error.message}")
                    _isLoading.value = false
                }
            })
        }
    }

    fun restoreNote(note: Note) {
        viewModelScope.launch {
            try {
                val userId = currentUserUseCase().first()?.uid ?: return@launch

                // Note'un id'si varsa Firebase'de güncelle
                note.id?.let { noteId ->
                    val noteRef = db.getReference(REFS_NOTES).child(noteId)

                    // Sadece isFavorite field'ını güncelle
                    noteRef.child("deleted").setValue(false)
                        .addOnSuccessListener {
                            Log.d("Firebase", "isDeleted durumu güncellendi: false")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "isDeleted güncelleme hatası: ${exception.message}")
                        }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Toggle isDeleted error: ${e.message}")
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