package com.example.myfoody.date.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.date.database.entities.FoodJokeEntity
import com.example.myfoody.date.database.entities.RecipesEntity

/**
 * Abstract class representing the RecipesDatabase, which is a Room database for storing recipe data.
 *
 * Defines the database configuration, including the entities, version, and TypeConverters.
 */
@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FoodJokeEntity::class], // Declare the RecipesEntity as an entity in the database
    version = 1, // Set the database version to 1
    exportSchema = false // Don't export the schema to avoid potential issues in the future
)
@TypeConverters(RecipesTypeConverter::class) // Use the RecipesTypeConverter for handling complex data types
abstract class RecipesDatabase : RoomDatabase() {

    /**
     * Provides access to the RecipesDao for performing database operations on the RecipesEntity.
     *
     * @return An instance of the RecipesDao.
     */
    abstract fun recipesDao(): RecipesDao

}