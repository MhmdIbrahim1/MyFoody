package com.example.myfoody.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int = 0,
    @SerializedName("cheap")
    val cheap: Boolean = false,
    @SerializedName("dairyFree")
    val dairyFree: Boolean = false,
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<IngredientDto>? = null,
    @SerializedName("glutenFree")
    val glutenFree: Boolean = false,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int = 0,
    @SerializedName("sourceName")
    val sourceName: String? = null,
    @SerializedName("sourceUrl")
    val sourceUrl: String? = null,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("vegan")
    val vegan: Boolean = false,
    @SerializedName("vegetarian")
    val vegetarian: Boolean = false,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean = false
)
