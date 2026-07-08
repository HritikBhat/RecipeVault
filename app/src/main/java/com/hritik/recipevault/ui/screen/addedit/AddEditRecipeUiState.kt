package com.hritik.recipevault.ui.screen.addedit

import com.hritik.recipevault.domain.model.Ingredient
import com.hritik.recipevault.domain.model.Step
import com.hritik.recipevault.util.UiText

data class AddEditRecipeUiState(
    val recipeName: String = "",
    val imageUri: String? = null,
    val cookingTime: String = "",
    val difficulty: String = "",
    val category: String = "",
    val collections: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList(),
    val createdAt: Long = 0,
    val isRecipeSaved: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
)
