package com.hritik.recipevault.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.data.local.datastore.UserPreferences
import com.hritik.recipevault.domain.model.User
import com.hritik.recipevault.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = userPreferences.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            userPreferences.clearUser()
            onSuccess()
        }
    }
}
