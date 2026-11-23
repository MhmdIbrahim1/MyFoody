package com.example.myfoody.data.remote.datasource

import com.example.myfoody.data.remote.api.FoodRecipesApi
import com.example.myfoody.data.remote.dto.FoodJokeDto
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import retrofit2.Response
import javax.inject.Inject

class RecipesRemoteDataSourceImpl @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) : RecipesRemoteDataSource {

    override suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipeResponseDto> {
        return foodRecipesApi.getRecipes(queries)
    }

    override suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipeResponseDto> {
        return foodRecipesApi.searchRecipes(searchQuery)
    }

    override suspend fun getFoodJoke(apiKey: String): Response<FoodJokeDto> {
        return foodRecipesApi.getFoodJoke(apiKey)
    }
}
