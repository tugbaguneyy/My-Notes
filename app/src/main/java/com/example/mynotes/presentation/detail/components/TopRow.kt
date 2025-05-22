package com.example.mynotes.presentation.detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mynotes.domain.model.Note

@Composable
fun TopRow(
    note: Note?,
    onFavoriteClick: (Boolean) -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(note?.isFavorite ?: false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Note değiştiğinde isFavorite'ı güncelle
    LaunchedEffect(note?.isFavorite) {
        isFavorite = note?.isFavorite ?: false
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sol taraf - Favorite ikonu
        IconButton(
            onClick = {
                isFavorite = !isFavorite
                onFavoriteClick(isFavorite)
            }
        ) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                modifier = Modifier.size(28.dp)
            )
        }

        // Sağ taraf - Update ve Delete butonları
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Update butonu
            IconButton(
                onClick = onUpdateClick
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Update note",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Delete butonu -> Alert dialog açar
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete note",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    // AlertDialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Move to Recycle Bin") },
            text = { Text("Do you want to move this note to the recycle bin?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteClick()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
