package com.example.myfoody.data.local.datasource

import com.example.myfoody.data.local.database.entity.FavoritesEntity
import com.example.myfoody.data.local.database.entity.FoodJokeEntity
import com.example.myfoody.data.local.database.entity.RecipesEntity
import kotlinx.coroutines.flow.Flow

interface RecipesLocalDataSource {
    fun readRecipes(): Flow<List<RecipesEntity>>
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)
    suspend fun deleteFavoriteRecipes(favoritesEntities: List<FavoritesEntity>)
    suspend fun deleteAllFavoriteRecipes()

    fun readFoodJoke(): Flow<List<FoodJokeEntity>>
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)
}
