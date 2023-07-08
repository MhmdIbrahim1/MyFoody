package com.example.myfoody.date.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.date.database.entities.FoodJokeEntity
import com.example.myfoody.date.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a Data Access Object (DAO) for interacting with the RecipesEntity in the Room database.
 *
 * Provides methods for inserting and querying recipe data within the database.
 */
@Dao
interface RecipesDao {

    /**
     * Inserts a RecipesEntity object into the database. In case of conflict, the new data will replace the old one.
     *
     * @param recipesEntity The RecipesEntity object to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)


    /**
     * Inserts a FavoritesEntity object into the database. In case of conflict, the new data will replace the old one.
     * @param favoritesEntity The FavoritesEntity object to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    /**
     * Inserts a FoodJokeEntity object into the database. In case of conflict, the new data will replace the old one.
     *
     * @param foodJokeEntity The FoodJokeEntity object to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)


    /**
     * Queries the database to retrieve all rows from the recipes_table, ordered by ID in ascending order.
     *
     * @return A Flow object containing a list of RecipesEntity objects.
     */
    @Query("SELECT * FROM recipes_table ORDER BY ID ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>


    /**
     * Queries the database to retrieve all rows from the favorites_recipes_table, ordered by ID in ascending order.
     *
     * @return A Flow object containing a list of FavoritesEntity objects.
     */

    @Query("SELECT * FROM favorite_recipes_table ORDER BY ID ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>


    /**
     * Queries the database to retrieve all rows from the food_joke_table, ordered by ID in ascending order.
     *
     * @return A Flow object containing a list of FoodJokeEntity objects.
     */
    @Query("SELECT * FROM food_joke_table ORDER BY ID ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    /**
     * Deletes a row from the recipes_table.
     */
    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    /**
     * Deletes all rows from the recipes_table.
     */
    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()


}