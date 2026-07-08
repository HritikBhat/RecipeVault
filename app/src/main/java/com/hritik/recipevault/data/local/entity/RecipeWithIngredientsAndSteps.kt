package com.hritik.recipevault.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredientsAndSteps(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<StepEntity>
)
