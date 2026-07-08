package com.hritik.recipevault.data.model

import com.hritik.recipevault.data.local.entity.CollectionEntity
import com.hritik.recipevault.data.local.entity.IngredientEntity
import com.hritik.recipevault.data.local.entity.RecipeEntity
import com.hritik.recipevault.data.local.entity.StepEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val recipes: List<RecipeEntity> = emptyList(),
    val ingredients: List<IngredientEntity> = emptyList(),
    val steps: List<StepEntity> = emptyList(),
    val collections: List<CollectionEntity> = emptyList(),
    val exportedAt: Long = System.currentTimeMillis()
)
