package com.example.myfoody.date.network

import com.example.myfoody.models.FoodJoke
import com.example.myfoody.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FoodRecipesApi {

    @GET("/recipes/complexSearch") // This is the endpoint we are calling
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String> // This is the query parameter we are passing in the endpoint call
    ): Response<FoodRecipe> // FoodRecipe is the data class we created in models/FoodRecipe.kt
    // Response is a generic class that takes in a type parameter. We are passing in FoodRecipe as the type parameter.
    // Response is a wrapper class that contains the response from the API call.

    @GET("/recipes/complexSearch")
     suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>


    @GET("food/jokes/random")
     suspend fun getFoodJoke(
        @Query("apiKey") apiKey: String
    ): Response<FoodJoke>
}