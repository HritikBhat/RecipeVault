package com.hritik.recipevault.ui.screen.home

import com.hritik.recipevault.domain.model.Recipe

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val categories: List<String> = listOf("All", "Breakfast", "Lunch", "Dinner", "Dessert"),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)
