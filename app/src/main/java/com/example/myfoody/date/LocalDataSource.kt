package com.example.myfoody.date

import com.example.myfoody.date.database.RecipesDao
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.date.database.entities.FoodJokeEntity
import com.example.myfoody.date.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Class representing a LocalDataSource, which is a wrapper for accessing data from the Room database.
 *
 * Provides methods for reading from and inserting into the database using the RecipesDao.
 */
class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao // Inject an instance of RecipesDao for database operations
) {

    /**
     * Reads data from the database and returns a Flow object containing a list of RecipesEntity objects.
     *
     * @return A Flow object containing a list of RecipesEntity objects.
     */
    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    /**
     * Reads data from the database and returns a Flow object containing a list of FavoritesEntity objects.
     *
     * @return A Flow object containing a list of FavoritesEntity objects.
     */
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    /**
     * Reads data from the database and returns a Flow object containing a list of FoodJokeEntity objects.
     *
     * @return A Flow object containing a list of FoodJokeEntity objects.
     */
    fun readFoodJoke(): Flow<List<FoodJokeEntity>> {
        return recipesDao.readFoodJoke()
    }

    /**
     * Inserts a RecipesEntity object into the database.
     *
     * @param recipesEntity The RecipesEntity object to insert into the database.
     */
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    /**
     * Inserts a FavoritesEntity object into the database.
     *
     * @param favoritesEntity The FavoritesEntity object to insert into the database.
     */

    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    /**
     * Inserts a FoodJokeEntity object into the database.
     *
     * @param foodJokeEntity The FoodJokeEntity object to insert into the database.
     */
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipesDao.insertFoodJoke(foodJokeEntity)
    }

    /**
     * Deletes a FavoritesEntity object from the database.
     *
     * @param favoritesEntity The FavoritesEntity object to delete from the database.
     */
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    /**
     * Deletes all rows from the recipes_table.
     */
    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

}