package com.hritik.recipevault.domain.model

data class Recipe(
    val id: Long = 0,
    val recipeName: String,
    val imageUri: String? = null,
    val cookingTime: String = "",
    val difficulty: String = "",
    val category: String = "All",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList()
)

data class Ingredient(
    val id: Long = 0,
    val recipeId: Long = 0,
    val ingredientName: String,
    val quantity: String = "",
    val unit: String = "",
    val position: Int
)

data class Step(
    val id: Long = 0,
    val recipeId: Long = 0,
    val description: String,
    val position: Int
)
