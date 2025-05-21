package com.example.mynotes.ui.auth

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
class AuthViewModel @Inject constructor(
    private val auth : FirebaseAuth
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean>
        get() = _isAuthenticated.asStateFlow()


    init {
        isUserAuthenticated()
    }

    fun signUp(email : String, password : String, passwordConfirmation : String){
        viewModelScope.launch {
            if (password == passwordConfirmation) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        _isAuthenticated.value = true
                    }
                    .addOnFailureListener {
                        _isAuthenticated.value = false
                    }
            }
        }
    }

    fun signIn(email : String, password : String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isAuthenticated.value = true
            }
            .addOnFailureListener {
                _isAuthenticated.value = false
            }
    }

    private fun isUserAuthenticated() {
        val isActive = auth.currentUser != null
        _isAuthenticated.value = isActive
    }
}