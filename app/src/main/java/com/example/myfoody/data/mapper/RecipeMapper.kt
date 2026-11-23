package com.example.myfoody.data.mapper

import com.example.myfoody.data.local.database.entity.RecipesEntity
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import com.example.myfoody.data.remote.dto.IngredientDto
import com.example.myfoody.data.remote.dto.RecipeDto
import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.domain.model.Recipe

fun IngredientDto.toDomain(): Ingredient = Ingredient(
    amount = amount,
    consistency = consistency.orEmpty(),
    image = image.orEmpty(),
    name = name.orEmpty(),
    original = original.orEmpty(),
    unit = unit.orEmpty()
)

fun Ingredient.toDto(): IngredientDto = IngredientDto(
    amount = amount,
    consistency = consistency,
    image = image,
    name = name,
    original = original,
    unit = unit
)

fun RecipeDto.toDomain(): Recipe = Recipe(
    id = id,
    title = title.orEmpty(),
    summary = summary.orEmpty(),
    image = image.orEmpty(),
    aggregateLikes = aggregateLikes,
    readyInMinutes = readyInMinutes,
    sourceName = sourceName,
    sourceUrl = sourceUrl.orEmpty(),
    cheap = cheap,
    dairyFree = dairyFree,
    glutenFree = glutenFree,
    vegan = vegan,
    vegetarian = vegetarian,
    veryHealthy = veryHealthy,
    extendedIngredients = extendedIngredients.orEmpty().map { it.toDomain() }
)

fun Recipe.toDto(): RecipeDto = RecipeDto(
    aggregateLikes = aggregateLikes,
    cheap = cheap,
    dairyFree = dairyFree,
    extendedIngredients = extendedIngredients.map { it.toDto() },
    glutenFree = glutenFree,
    id = id,
    image = image,
    readyInMinutes = readyInMinutes,
    sourceName = sourceName,
    sourceUrl = sourceUrl,
    summary = summary,
    title = title,
    vegan = vegan,
    vegetarian = vegetarian,
    veryHealthy = veryHealthy
)

fun FoodRecipeResponseDto.toDomainList(): List<Recipe> =
    results.orEmpty().map { it.toDomain() }

fun RecipesEntity.toDomainList(): List<Recipe> =
    foodRecipe.toDomainList()

fun List<Recipe>.toRecipesEntity(): RecipesEntity = RecipesEntity(
    foodRecipe = FoodRecipeResponseDto(results = map { it.toDto() })
)
