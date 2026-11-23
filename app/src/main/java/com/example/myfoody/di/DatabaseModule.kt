package com.example.myfoody.di

import android.content.Context
import androidx.room.Room
import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.local.database.RecipesDao
import com.example.myfoody.data.local.database.RecipesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRecipesDatabase(
        @ApplicationContext context: Context
    ): RecipesDatabase = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        DataConstants.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRecipesDao(database: RecipesDatabase): RecipesDao = database.recipesDao()
}