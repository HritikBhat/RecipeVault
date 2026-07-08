package com.hritik.recipevault.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hritik.recipevault.data.local.dao.CollectionDao
import com.hritik.recipevault.data.local.dao.RecipeDao
import com.hritik.recipevault.data.local.database.RecipeDatabase
import com.hritik.recipevault.data.local.datastore.UserPreferences
import com.hritik.recipevault.data.repository.AuthRepositoryImpl
import com.hritik.recipevault.data.repository.BackupRepositoryImpl
import com.hritik.recipevault.data.repository.CollectionRepository
import com.hritik.recipevault.data.repository.CollectionRepositoryImpl
import com.hritik.recipevault.data.repository.RecipeRepository
import com.hritik.recipevault.data.repository.RecipeRepositoryImpl
import com.hritik.recipevault.domain.repository.AuthRepository
import com.hritik.recipevault.domain.repository.BackupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideBackupRepository(recipeDao: RecipeDao, collectionDao: CollectionDao): BackupRepository {
        return BackupRepositoryImpl(recipeDao, collectionDao)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore, userPreferences)
    }
}
