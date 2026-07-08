package com.hritik.recipevault.data.repository

import com.hritik.recipevault.domain.model.Collection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getAllCollections(): Flow<List<Collection>>
    suspend fun insertCollection(collection: Collection)
    suspend fun updateCollection(collection: Collection)
    suspend fun deleteCollection(collection: Collection)
}
