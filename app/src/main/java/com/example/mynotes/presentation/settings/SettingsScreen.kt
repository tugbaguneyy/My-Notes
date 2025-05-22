package com.example.mynotes.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mynotes.navigation.Screen


@Composable
fun SettingsScreen(
    navController: NavController
) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    Column {
        Button(
            onClick = {
                viewModel.signOut()
                navController.navigate(Screen.Login)
            }
        ) {
            Text(text = "Sign Out")
        }
    }
}