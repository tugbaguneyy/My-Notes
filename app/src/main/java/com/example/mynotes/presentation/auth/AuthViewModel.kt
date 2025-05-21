package com.example.mynotes.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.example.mynotes.domain.usecase.SignUpWithEmailAndPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signUpWithEmailAndPasswordUseCase: SignUpWithEmailAndPasswordUseCase,
    private val currentUserUseCase: CurrentUserUseCase
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
                signUpWithEmailAndPasswordUseCase(email, password).collect{
                    _isAuthenticated.value = it
                }
            }
        }
    }

    fun signIn(email : String, password : String){
        viewModelScope.launch {
            signInWithEmailAndPasswordUseCase(email, password).collect{
                _isAuthenticated.value = it
            }
        }
    }

    private fun isUserAuthenticated() {
        viewModelScope.launch {
            currentUserUseCase().collect { it ->
                _isAuthenticated.value = it != null
            }
        }
    }
}