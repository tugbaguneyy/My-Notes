package com.example.mynotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mynotes.navigation.Screen.Home
import com.example.mynotes.navigation.Screen.Detail
import com.example.mynotes.navigation.Screen.Settings
import com.example.mynotes.presentation.add.AddScreen
import com.example.mynotes.presentation.auth.LoginScreen
import com.example.mynotes.presentation.auth.RegisterScreen
import com.example.mynotes.presentation.home.HomeScreen
import com.example.mynotes.presentation.detail.DetailScreen
import com.example.mynotes.presentation.settings.SettingsScreen
import com.example.mynotes.presentation.trash.TrashScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Home> {
           HomeScreen(navController)
        }
        composable("${Screen.Detail}/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            DetailScreen(navController, noteId)
        }
        composable<Settings> {
           SettingsScreen(navController)
        }
        composable<Screen.Register> {
            RegisterScreen(navController)
        }
        composable<Screen.Login>{
            LoginScreen(navController)
        }
        composable<Screen.Add>{
            AddScreen(navController)
        }
        composable<Screen.Trash>{
            TrashScreen()
        }
    }
}