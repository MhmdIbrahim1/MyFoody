package com.example.myfoody.domain.repository

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun observeCachedRecipes(): Flow<List<Recipe>>
    suspend fun fetchRecipes(filter: MealAndDietFilter): AppResult<List<Recipe>>
    suspend fun searchRecipes(query: String): AppResult<List<Recipe>>
}
