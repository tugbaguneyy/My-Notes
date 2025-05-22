package com.example.mynotes.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val currentUserUseCase: CurrentUserUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean>
        get() = _isAuthenticated.asStateFlow()


    init {
        isUserAuthenticated()
    }

    fun signOut(){
        viewModelScope.launch {
            signOutUseCase()
            _isAuthenticated.value = false
        }
    }

    private fun isUserAuthenticated() {
        viewModelScope.launch {
            val isActive = currentUserUseCase().first() != null
            _isAuthenticated.value = isActive
        }
    }
}