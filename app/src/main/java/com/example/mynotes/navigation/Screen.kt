package com.example.mynotes.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Detail : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Add : Screen
}