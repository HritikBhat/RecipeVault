package com.hritik.recipevault.data.repository

import com.hritik.recipevault.data.local.dao.RecipeDao
import com.hritik.recipevault.data.local.entity.IngredientEntity
import com.hritik.recipevault.data.local.entity.RecipeEntity
import com.hritik.recipevault.data.local.entity.StepEntity
import com.hritik.recipevault.domain.model.Ingredient
import com.hritik.recipevault.domain.model.Recipe
import com.hritik.recipevault.domain.model.Step
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val dao: RecipeDao
) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipes().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecipeById(id: Long): Flow<Recipe?> {
        return dao.getRecipeById(id).map { it?.toDomain() }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val recipeEntity = recipe.toEntity()
        val ingredientEntities = recipe.ingredients.map { it.toEntity(recipe.id) }
        val stepEntities = recipe.steps.map { it.toEntity(recipe.id) }
        
        if (recipe.id == 0L) {
            dao.insertFullRecipe(recipeEntity, ingredientEntities, stepEntities)
        } else {
            dao.updateFullRecipe(recipeEntity, ingredientEntities, stepEntities)
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        dao.deleteRecipe(recipe.toEntity())
    }
}

// Mappers
fun com.hritik.recipevault.data.local.entity.RecipeWithIngredientsAndSteps.toDomain(): Recipe {
    return Recipe(
        id = recipe.id,
        recipeName = recipe.recipeName,
        imageUri = recipe.imageUri,
        cookingTime = recipe.cookingTime,
        difficulty = recipe.difficulty,
        category = recipe.category,
        isFavorite = recipe.isFavorite,
        createdAt = recipe.createdAt,
        updatedAt = recipe.updatedAt,
        ingredients = ingredients.sortedBy { it.position }.map { it.toDomain() },
        steps = steps.sortedBy { it.position }.map { it.toDomain() }
    )
}

fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        recipeId = recipeId,
        ingredientName = ingredientName,
        position = position
    )
}

fun StepEntity.toDomain(): Step {
    return Step(
        id = id,
        recipeId = recipeId,
        description = description,
        position = position
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        recipeName = recipeName,
        imageUri = imageUri,
        cookingTime = cookingTime,
        difficulty = difficulty,
        category = category,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}

fun Ingredient.toEntity(recipeId: Long): IngredientEntity {
    return IngredientEntity(
        id = id,
        recipeId = recipeId,
        ingredientName = ingredientName,
        position = position
    )
}

fun Step.toEntity(recipeId: Long): StepEntity {
    return StepEntity(
        id = id,
        recipeId = recipeId,
        description = description,
        position = position
    )
}
