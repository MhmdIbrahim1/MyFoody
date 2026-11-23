package com.example.myfoody.data.mapper

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteMapperTest {

    private val sampleRecipe = Recipe(
        id = 55,
        title = "Avocado Toast",
        summary = "Quick breakfast",
        image = "https://example.com/toast.jpg",
        aggregateLikes = 18,
        readyInMinutes = 10,
        sourceName = null,
        sourceUrl = "https://example.com/toast",
        cheap = false,
        dairyFree = true,
        glutenFree = false,
        vegan = true,
        vegetarian = true,
        veryHealthy = true,
        extendedIngredients = listOf(
            Ingredient(
                amount = 1.0,
                consistency = "solid",
                image = "avocado.jpg",
                name = "avocado",
                original = "1 ripe avocado",
                unit = "whole"
            )
        )
    )

    @Test
    fun `favorite recipe round-trips through FavoritesEntity`() {
        val favorite = FavoriteRecipe(id = 42, recipe = sampleRecipe)

        val roundTrip = favorite.toEntity().toDomain()

        assertEquals(favorite, roundTrip)
    }

    @Test
    fun `recipe converts to FavoritesEntity with default zero id`() {
        val entity = sampleRecipe.toFavoriteEntity()

        assertEquals(0, entity.id)
        assertEquals(sampleRecipe, entity.toDomain().recipe)
    }
}
