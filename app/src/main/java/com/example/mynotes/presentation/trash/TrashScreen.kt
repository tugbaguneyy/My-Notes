package com.example.mynotes.presentation.trash

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.mynotes.presentation.trash.components.EmptyTrashContent
import com.example.mynotes.presentation.trash.components.TrashNoteItem

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen() {
    val viewModel: TrashViewModel = hiltViewModel()
    val deletedNotes by viewModel.deletedNotes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                deletedNotes.isEmpty() -> {
                    EmptyTrashContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(deletedNotes) { note ->
                            TrashNoteItem(
                                note = note,
                                onRestore = { viewModel.restoreNote(note) },
                                onDelete = { viewModel.permanentlyDeleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }




