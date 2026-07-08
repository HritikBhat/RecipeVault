package com.hritik.recipevault.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            _state.update { it.copy(error = "Google Sign In failed") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signInWithGoogle(idToken)
            result.onSuccess {
                _state.update { it.copy(isLoading = false, isLoginSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onError(message: String) {
        _state.update { it.copy(error = message, isLoading = false) }
    }

    fun resetError() {
        _state.update { it.copy(error = null) }
    }
}
