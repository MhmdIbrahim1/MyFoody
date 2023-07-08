package com.example.myfoody.di

import android.content.Context
import androidx.room.Room
import com.example.myfoody.date.database.RecipesDatabase
import com.example.myfoody.util.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Object representing a DatabaseModule, which is a Dagger module for providing database-related dependencies.
 *
 * Provides methods for creating and injecting instances of RecipesDatabase and its DAO.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of the RecipesDatabase.
     *
     * @param context The application context required for building the Room database.
     * @return An instance of the RecipesDatabase.
     */
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        DATABASE_NAME
    ).build()

    /**
     * Provides a singleton instance of the RecipesDao for interacting with the RecipesDatabase.
     *
     * @param database The instance of the RecipesDatabase.
     * @return An instance of the RecipesDao.
     */
    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase) = database.recipesDao()

}