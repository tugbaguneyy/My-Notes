package com.example.mynotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth : FirebaseAuth
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean>
        get() = _isAuthenticated.asStateFlow()

    init {
        isUserAuthenticated()
    }


    fun signOut(){
        viewModelScope.launch {
            auth.signOut()
            _isAuthenticated.value = false
        }
    }

    private fun isUserAuthenticated(){
        val isActive = auth.currentUser != null
        _isAuthenticated.value = isActive
    }
}