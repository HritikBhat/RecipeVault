package com.hritik.recipevault.ui.screen.detail

import com.hritik.recipevault.domain.model.Recipe

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
