package com.example.myfoody.presentation.navigation

import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeParcelableMapperTest {

    @Test
    fun `recipe maps to parcelable and back without data loss`() {
        val original = Recipe(
            id = 101,
            title = "Roasted Carrots",
            summary = "Sweet roasted carrots",
            image = "https://example.com/carrots.jpg",
            aggregateLikes = 42,
            readyInMinutes = 25,
            sourceName = null,
            sourceUrl = "https://example.com/recipe/101",
            cheap = true,
            dairyFree = true,
            glutenFree = true,
            vegan = true,
            vegetarian = true,
            veryHealthy = true,
            extendedIngredients = listOf(
                Ingredient(
                    amount = 200.0,
                    consistency = "solid",
                    image = "carrots.jpg",
                    name = "carrots",
                    original = "200g fresh carrots",
                    unit = "grams"
                )
            )
        )

        val roundTrip = original.toParcelable().toDomain()

        assertEquals(original, roundTrip)
    }
}
