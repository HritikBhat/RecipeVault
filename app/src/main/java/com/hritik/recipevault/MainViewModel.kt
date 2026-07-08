package com.hritik.recipevault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.data.local.datastore.UserPreferences
import com.hritik.recipevault.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isLoggedIn = userPreferences.isUserLoggedIn.first()
            _startDestination.value = if (isLoggedIn) Screen.Home.route else Screen.Login.route
        }
    }
}
