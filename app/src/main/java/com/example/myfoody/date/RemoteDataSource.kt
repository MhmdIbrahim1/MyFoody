package com.example.myfoody.date

import com.example.myfoody.date.network.FoodRecipesApi
import com.example.myfoody.models.FoodJoke
import com.example.myfoody.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

/**
 * RemoteDataSource class that provides access to the FoodRecipesApi.
 *
 * @property foodRecipesApi An instance of FoodRecipesApi for making API requests.
 */
class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    /**
     * Retrieves recipes from the API using the provided query parameters.
     *
     * @param queries A map of query parameters to include in the API request.
     * @return A Response object containing a FoodRecipe object if the request is successful.
     */
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }

    /**
     * Retrieves recipes from the API using the provided query parameters.
     *
     * @param searchQuery A map of query parameters to include in the API request.
     * @return A Response object containing a FoodRecipe object if the request is successful.
     */
    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(searchQuery)
    }

    /**
     * Retrieves a random food joke from the API.
     *
     * @param apiKey The API key to use in the request.
     * @return A Response object containing a FoodJoke object if the request is successful.
     */
    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return foodRecipesApi.getFoodJoke(apiKey)
    }
}