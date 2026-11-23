package com.example.myfoody.domain.repository

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteRecipe>>
    suspend fun addFavorite(recipe: Recipe)
    suspend fun deleteFavorite(favoriteRecipe: FavoriteRecipe)
    suspend fun deleteFavorites(favoriteRecipes: List<FavoriteRecipe>)
    suspend fun deleteAllFavorites()
}
