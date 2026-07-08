package com.hritik.recipevault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hritik.recipevault.data.local.dao.CollectionDao
import com.hritik.recipevault.data.local.dao.RecipeDao
import com.hritik.recipevault.data.local.entity.CollectionEntity
import com.hritik.recipevault.data.local.entity.IngredientEntity
import com.hritik.recipevault.data.local.entity.RecipeEntity
import com.hritik.recipevault.data.local.entity.StepEntity

@Database(
    entities = [RecipeEntity::class, IngredientEntity::class, StepEntity::class, CollectionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract val recipeDao: RecipeDao
    abstract val collectionDao: CollectionDao
}
