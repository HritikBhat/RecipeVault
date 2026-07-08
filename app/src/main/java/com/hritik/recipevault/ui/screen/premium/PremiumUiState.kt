package com.hritik.recipevault.ui.screen.premium

import com.hritik.recipevault.util.UiText

sealed class PremiumUiState {
    object Idle : PremiumUiState()
    object Loading : PremiumUiState()
    object Success : PremiumUiState()
    data class Error(val uiText: UiText) : PremiumUiState()
}
