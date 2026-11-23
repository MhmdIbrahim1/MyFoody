package com.example.myfoody.data.local.database

import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import com.example.myfoody.data.remote.dto.IngredientDto
import com.example.myfoody.data.remote.dto.RecipeDto
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipesTypeConverterTest {

    private val converter = RecipesTypeConverter()

    @Test
    fun `converts FoodRecipeResponseDto and RecipeDto to JSON and back`() {
        val recipe = RecipeDto(
            aggregateLikes = 10,
            cheap = true,
            dairyFree = false,
            extendedIngredients = listOf(
                IngredientDto(
                    amount = 2.0,
                    consistency = "liquid",
                    image = "olive-oil.jpg",
                    name = "olive oil",
                    original = "2 tbsp olive oil",
                    unit = "tbsp"
                )
            ),
            glutenFree = true,
            id = 9,
            image = "https://example.com/pasta.jpg",
            readyInMinutes = 20,
            sourceName = "Chef",
            sourceUrl = "https://example.com/pasta",
            summary = "Simple pasta",
            title = "Pasta",
            vegan = false,
            vegetarian = true,
            veryHealthy = true
        )
        val response = FoodRecipeResponseDto(results = listOf(recipe))

        val responseJson = converter.foodRecipeToString(response)
        assertEquals(response, converter.stringToFoodRecipe(responseJson))

        val recipeJson = converter.recipeToString(recipe)
        assertEquals(recipe, converter.stringToRecipe(recipeJson))
    }
}
