package com.hritik.recipevault.di

import android.app.Application
import androidx.room.Room
import com.hritik.recipevault.data.local.dao.CollectionDao
import com.hritik.recipevault.data.local.dao.RecipeDao
import com.hritik.recipevault.data.local.database.RecipeDatabase
import com.hritik.recipevault.data.repository.CollectionRepository
import com.hritik.recipevault.data.repository.CollectionRepositoryImpl
import com.hritik.recipevault.data.repository.RecipeRepository
import com.hritik.recipevault.data.repository.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            app,
            RecipeDatabase::class.java,
            "recipe_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(db: RecipeDatabase): RecipeDao {
        return db.recipeDao
    }

    @Provides
    @Singleton
    fun provideCollectionDao(db: RecipeDatabase): CollectionDao {
        return db.collectionDao
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(dao: RecipeDao): RecipeRepository {
        return RecipeRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(dao: CollectionDao): CollectionRepository {
        return CollectionRepositoryImpl(dao)
    }
}
