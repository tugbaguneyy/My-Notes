package com.example.mynotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mynotes.navigation.Screen.Auth
import com.example.mynotes.navigation.Screen.Home
import com.example.mynotes.navigation.Screen.Detail
import com.example.mynotes.navigation.Screen.Settings
import com.example.mynotes.ui.auth.AuthScreen
import com.example.mynotes.ui.home.HomeScreen
import com.example.mynotes.ui.detail.DetailScreen
import com.example.mynotes.ui.settings.SettingsScreen

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
        composable<Auth> {
           AuthScreen(navController)
        }
        composable<Home> {
           HomeScreen(navController)
        }
        composable<Detail> {
           DetailScreen(navController)
        }
        composable<Settings> {
           SettingsScreen(navController)
        }
    }
}