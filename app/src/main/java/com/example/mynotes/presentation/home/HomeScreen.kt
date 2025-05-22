package com.example.mynotes.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mynotes.navigation.Screen

@Composable
fun HomeScreen(navController: NavController
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val isUserAuthenticated = viewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isUserAuthenticated.value) {
        if (!isUserAuthenticated.value){
            navController.navigate(Screen.Login)
        }
    }

    Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
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
    }
}