package com.example.myfoody.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodRecipeResponseDto(
    @SerializedName("results")
    val results: List<RecipeDto>? = null
)
