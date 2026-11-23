package com.example.myfoody.data.remote.datasource

import com.example.myfoody.data.remote.dto.FoodJokeDto
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import retrofit2.Response

interface RecipesRemoteDataSource {
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipeResponseDto>
    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipeResponseDto>
    suspend fun getFoodJoke(apiKey: String): Response<FoodJokeDto>
}
