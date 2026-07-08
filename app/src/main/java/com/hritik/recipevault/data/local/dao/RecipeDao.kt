package com.hritik.recipevault.data.local.dao

import androidx.room.*
import com.hritik.recipevault.data.local.entity.IngredientEntity
import com.hritik.recipevault.data.local.entity.RecipeEntity
import com.hritik.recipevault.data.local.entity.RecipeWithIngredientsAndSteps
import com.hritik.recipevault.data.local.entity.StepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeWithIngredientsAndSteps>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Long): Flow<RecipeWithIngredientsAndSteps?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<StepEntity>)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Long)

    @Query("DELETE FROM steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsByRecipeId(recipeId: Long)

    @Transaction
    suspend fun insertFullRecipe(recipe: RecipeEntity, ingredients: List<IngredientEntity>, steps: List<StepEntity>) {
        val recipeId = insertRecipe(recipe)
        
        // Ensure child entities have the correct recipeId
        val updatedIngredients = ingredients.map { it.copy(recipeId = recipeId) }
        val updatedSteps = steps.map { it.copy(recipeId = recipeId) }
        
        insertIngredients(updatedIngredients)
        insertSteps(updatedSteps)
    }

    @Transaction
    suspend fun updateFullRecipe(recipe: RecipeEntity, ingredients: List<IngredientEntity>, steps: List<StepEntity>) {
        insertRecipe(recipe)
        deleteIngredientsByRecipeId(recipe.id)
        deleteStepsByRecipeId(recipe.id)
        insertIngredients(ingredients.map { it.copy(recipeId = recipe.id) })
        insertSteps(steps.map { it.copy(recipeId = recipe.id) })
    }
}
