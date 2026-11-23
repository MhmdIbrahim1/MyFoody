package com.example.myfoody.presentation.navigation

import android.os.Parcelable
import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.domain.model.Recipe
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientParcelable(
    val amount: Double,
    val consistency: String,
    val image: String,
    val name: String,
    val original: String,
    val unit: String
) : Parcelable

@Parcelize
data class RecipeParcelable(
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
    val extendedIngredients: List<IngredientParcelable>
) : Parcelable

fun Ingredient.toParcelable(): IngredientParcelable = IngredientParcelable(
    amount = amount,
    consistency = consistency,
    image = image,
    name = name,
    original = original,
    unit = unit
)

fun IngredientParcelable.toDomain(): Ingredient = Ingredient(
    amount = amount,
    consistency = consistency,
    image = image,
    name = name,
    original = original,
    unit = unit
)

fun Recipe.toParcelable(): RecipeParcelable = RecipeParcelable(
    id = id,
    title = title,
    summary = summary,
    image = image,
    aggregateLikes = aggregateLikes,
    readyInMinutes = readyInMinutes,
    sourceName = sourceName,
    sourceUrl = sourceUrl,
    cheap = cheap,
    dairyFree = dairyFree,
    glutenFree = glutenFree,
    vegan = vegan,
    vegetarian = vegetarian,
    veryHealthy = veryHealthy,
    extendedIngredients = extendedIngredients.map { it.toParcelable() }
)

fun RecipeParcelable.toDomain(): Recipe = Recipe(
    id = id,
    title = title,
    summary = summary,
    image = image,
    aggregateLikes = aggregateLikes,
    readyInMinutes = readyInMinutes,
    sourceName = sourceName,
    sourceUrl = sourceUrl,
    cheap = cheap,
    dairyFree = dairyFree,
    glutenFree = glutenFree,
    vegan = vegan,
    vegetarian = vegetarian,
    veryHealthy = veryHealthy,
    extendedIngredients = extendedIngredients.map { it.toDomain() }
)
