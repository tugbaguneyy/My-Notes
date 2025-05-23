package com.example.mynotes.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mynotes.navigation.Screen
import com.example.mynotes.presentation.settings.components.SettingsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    val viewModel = hiltViewModel<SettingsViewModel>()

    //var darkMode = viewModel.isDarkMode.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Actions",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )


        // Logout
        SettingsItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Sign out",
            subtitle = "Sign out of your account",
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            onClick = { showLogoutDialog = true }
        )

        // Settings Items
        SettingsItem(
            icon = Icons.Outlined.Delete,
            title = "Trash",
            subtitle = "Manage your trash",
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            onClick = {navController.navigate(Screen.Trash)  }
        )

//        // Theme Settings
//        SettingsItem(
//            icon = Icons.Outlined.DarkMode,
//            title = "Dark Mode",
//            subtitle = "Enable dark mode",
//            trailingContent = {
//                Switch(
//                    checked = darkMode.value,
//                    onCheckedChange = {
//                        viewModel.setDarkMode(it)
//                    },
//                    colors = SwitchDefaults.colors(
//                        checkedThumbColor = MaterialTheme.colorScheme.primary,
//                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
//                    )
//                )
//            }
//        )

    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.signOut()
                        navController.navigate(Screen.Login)
                    }
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}