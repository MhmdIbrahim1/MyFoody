package com.example.myfoody.domain.model

data class Recipe(
    val id: Int,
    val title: String,
    val summary: String,
    val image: String,
    val aggregateLikes: Int,
    val readyInMinutes: Int,
    val sourceName: String?,
    val sourceUrl: String,
    val cheap: Boolean,
    val dairyFree: Boolean,
    val glutenFree: Boolean,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
    val extendedIngredients: List<Ingredient>
)
