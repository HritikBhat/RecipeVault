package com.hritik.recipevault.data.repository

import com.hritik.recipevault.data.local.dao.CollectionDao
import com.hritik.recipevault.data.local.entity.CollectionEntity
import com.hritik.recipevault.domain.model.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val dao: CollectionDao
) : CollectionRepository {

    override fun getAllCollections(): Flow<List<Collection>> {
        return dao.getAllCollections().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCollection(collection: Collection) {
        dao.insertCollection(collection.toEntity())
    }

    override suspend fun updateCollection(collection: Collection) {
        dao.updateCollection(collection.toEntity())
    }

    override suspend fun deleteCollection(collection: Collection) {
        dao.deleteCollection(collection.toEntity())
    }
}

// Mappers
fun CollectionEntity.toDomain(): Collection {
    return Collection(
        id = id,
        name = name,
        createdAt = createdAt
    )
}

fun Collection.toEntity(): CollectionEntity {
    return CollectionEntity(
        id = id,
        name = name,
        createdAt = createdAt
    )
}
