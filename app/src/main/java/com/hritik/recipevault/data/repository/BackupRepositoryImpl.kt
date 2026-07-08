package com.hritik.recipevault.data.repository

import com.hritik.recipevault.data.local.dao.CollectionDao
import com.hritik.recipevault.data.local.dao.RecipeDao
import com.hritik.recipevault.data.model.BackupData
import com.hritik.recipevault.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val collectionDao: CollectionDao
) : BackupRepository {

    override suspend fun getBackupData(): BackupData = withContext(Dispatchers.IO) {
        val recipes = recipeDao.getAllRecipesList().map { it.recipe.copy(imageUri = null) }
        val ingredients = recipeDao.getAllRecipesList().flatMap { it.ingredients }
        val steps = recipeDao.getAllRecipesList().flatMap { it.steps }
        val collections = collectionDao.getAllCollectionsList()

        BackupData(
            version = 1,
            recipes = recipes,
            ingredients = ingredients,
            steps = steps,
            collections = collections
        )
    }

    override suspend fun restoreBackupData(backupData: BackupData) = withContext(Dispatchers.IO) {
        // Clear existing data
        recipeDao.deleteAllIngredients()
        recipeDao.deleteAllSteps()
        recipeDao.deleteAllRecipes()
        collectionDao.deleteAllCollections()

        // Restore collections
        collectionDao.insertCollections(backupData.collections)

        // Restore recipes, ingredients, and steps
        // Since we cleared everything, we can just insert them.
        // However, we need to be careful about foreign keys if IDs changed, 
        // but here we are restoring with original IDs if possible or relying on Room to handle it.
        // Actually, if we insert entities with IDs, Room will use them.
        
        backupData.recipes.forEach { recipe ->
            recipeDao.insertRecipe(recipe)
        }
        
        recipeDao.insertIngredients(backupData.ingredients)
        recipeDao.insertSteps(backupData.steps)
    }
}
