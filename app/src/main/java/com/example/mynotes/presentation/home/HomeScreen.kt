package com.example.mynotes.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mynotes.navigation.Screen
import com.example.mynotes.presentation.home.components.NoteCard

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = hiltViewModel<HomeViewModel>()

    val notes = viewModel.notes.collectAsStateWithLifecycle()
    val isUserAuthenticated = viewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isUserAuthenticated.value) {
        if (!isUserAuthenticated.value){
            navController.navigate(Screen.Login)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home", fontSize = 24.sp)

        Button(
            onClick = {
                viewModel.signOut()
            }
        ) {
            Text(text = "Sign Out")
        }

        Button(
            onClick = {
                navController.navigate(Screen.Add)
            }
        ) {
            Text(text = "Add")
        }

        // Grid yapısı için LazyVerticalGrid kullanıyoruz
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Yan yana 2 tane
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes.value) { note ->
                NoteCard(
                    note = note,
                    onNoteClick = { selectedNote ->
                        // TODO: Note detay sayfasına navigate
                    },
                    onFavoriteClick = { selectedNote, isFavorite ->
                        // TODO: Favorite durumunu güncelle
                    }
                )
            }
        }
    }
}