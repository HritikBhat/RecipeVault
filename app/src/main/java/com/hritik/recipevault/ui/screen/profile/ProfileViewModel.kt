package com.hritik.recipevault.ui.screen.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.data.local.datastore.UserPreferences
import com.hritik.recipevault.data.model.BackupData
import com.hritik.recipevault.domain.model.User
import com.hritik.recipevault.domain.repository.AuthRepository
import com.hritik.recipevault.domain.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    val user: StateFlow<User?> = userPreferences.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            userPreferences.clearUser()
            onSuccess()
        }
    }

    fun exportData(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            try {
                val backupData = backupRepository.getBackupData()
                val jsonString = Json.encodeToString(backupData)
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                _uiState.value = BackupUiState.Success("Data exported successfully")
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error("Failed to export data: ${e.message}")
            }
        }
    }

    fun importData(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            try {
                val jsonString = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (jsonString != null) {
                    val backupData = Json.decodeFromString<BackupData>(jsonString)
                    
                    // Basic validation
                    if (backupData.version > 0) {
                        backupRepository.restoreBackupData(backupData)
                        _uiState.value = BackupUiState.Success("Data imported successfully")
                    } else {
                        _uiState.value = BackupUiState.Error("Invalid backup file. Please select a valid backup exported from this app.")
                    }
                } else {
                    _uiState.value = BackupUiState.Error("Could not read file.")
                }
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error("Invalid backup file. Please select a valid backup exported from this app.")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = BackupUiState.Idle
    }
}

sealed class BackupUiState {
    object Idle : BackupUiState()
    object Loading : BackupUiState()
    data class Success(val message: String) : BackupUiState()
    data class Error(val message: String) : BackupUiState()
}
