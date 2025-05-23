package com.example.mynotes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mynotes.navigation.NavigationGraph
import com.example.mynotes.navigation.Screen
import com.example.mynotes.presentation.trash.TrashViewModel
import com.example.mynotes.ui.theme.MyappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showClearAllDialog by remember { mutableStateOf(false) }
            val viewModel: TrashViewModel = hiltViewModel()
            MyappTheme {
                val navController = rememberNavController()
                val startDestination = Screen.Login
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                fun isCurrentScreen(screen: KClass<out Screen>): Boolean {
                    return currentDestination?.hierarchy?.any {
                        it.route?.contains(screen.simpleName ?: "") == true
                    } == true
                }

                val isAuthScreen = isCurrentScreen(Screen.Login::class) || isCurrentScreen(Screen.Register::class)

                Scaffold(
                    topBar = {
                        if (!isAuthScreen) {
                            val title = when {
                                isCurrentScreen(Screen.Home::class) -> "My Notes 📝"
                                isCurrentScreen(Screen.Settings::class) -> "Settings"
                                isCurrentScreen(Screen.Add::class) -> "Add Note"
                                isCurrentScreen(Screen.Detail::class) -> "Note Detail"
                                isCurrentScreen(Screen.Trash::class) -> "Trash"
                                else -> "Not Found"
                            }

                            CenterAlignedTopAppBar(
                                title = {
                                    Text(title)
                                },
                                navigationIcon = {
                                    if (!isCurrentScreen(Screen.Home::class)){
                                        IconButton(
                                            onClick = {
                                                navController.navigateUp()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Back"
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    if (isCurrentScreen(Screen.Home::class)) {
                                        Box {
                                            IconButton(onClick = { navController.navigate(Screen.Settings) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = "Menu"
                                                )
                                            }
                                        }
                                    }
                                    if(isCurrentScreen(Screen.Trash::class)){
                                        Box{
                                            IconButton(
                                                onClick = { showClearAllDialog = true }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear All"
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (isCurrentScreen(Screen.Home::class)) {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(Screen.Add)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Expense"
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavigationGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            // Clear All Dialog
            if (showClearAllDialog) {
                AlertDialog(
                    onDismissRequest = { showClearAllDialog = false },
                    title = { Text("Clear All Trash") },
                    text = { Text("This will permanently delete all notes in trash. This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.clearAllTrash()
                                showClearAllDialog = false
                            }
                        ) {
                            Text("Clear All")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showClearAllDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}