package com.example.myfoody.data.mapper

import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import com.example.myfoody.data.remote.dto.IngredientDto
import com.example.myfoody.data.remote.dto.RecipeDto
import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeMapperTest {

    @Test
    fun `recipe dto with null fields maps safely to domain defaults`() {
        val dto = RecipeDto(
            id = 7,
            title = null,
            summary = null,
            image = null,
            sourceName = null,
            sourceUrl = null,
            extendedIngredients = listOf(
                IngredientDto(
                    amount = 1.5,
                    consistency = null,
                    image = null,
                    name = null,
                    original = null,
                    unit = null
                )
            )
        )

        val domain = dto.toDomain()

        assertEquals(7, domain.id)
        assertEquals("", domain.title)
        assertEquals("", domain.summary)
        assertEquals("", domain.image)
        assertEquals(null, domain.sourceName)
        assertEquals("", domain.sourceUrl)
        assertEquals(1, domain.extendedIngredients.size)
        assertEquals(
            Ingredient(
                amount = 1.5,
                consistency = "",
                image = "",
                name = "",
                original = "",
                unit = ""
            ),
            domain.extendedIngredients.first()
        )
    }

    @Test
    fun `response dto with null results maps to empty list`() {
        val response = FoodRecipeResponseDto(results = null)
        assertTrue(response.toDomainList().isEmpty())
    }

    @Test
    fun `recipe list round-trips through RecipesEntity without data loss`() {
        val recipes = listOf(
            Recipe(
                id = 12,
                title = "Tomato Soup",
                summary = "Warm tomato soup",
                image = "https://example.com/soup.jpg",
                aggregateLikes = 99,
                readyInMinutes = 30,
                sourceName = "Kitchen",
                sourceUrl = "https://example.com/soup",
                cheap = true,
                dairyFree = true,
                glutenFree = true,
                vegan = true,
                vegetarian = true,
                veryHealthy = false,
                extendedIngredients = listOf(
                    Ingredient(
                        amount = 3.0,
                        consistency = "solid",
                        image = "tomato.png",
                        name = "tomato",
                        original = "3 fresh tomatoes",
                        unit = "pcs"
                    )
                )
            )
        )

        val entity = recipes.toRecipesEntity()
        assertEquals(0, entity.id)
        assertEquals(recipes, entity.toDomainList())
    }
}
