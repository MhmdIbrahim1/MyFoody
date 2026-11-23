package com.example.myfoody.ui.fragments.favorite

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.models.Result
import com.example.myfoody.ui.theme.MyFoodyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoriteRecipesScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun showsEmptyStateAndDispatchesDistinctTapAndLongPressEvents() {
        val favorite = FavoritesEntity(42, Result(
            aggregateLikes = 0, cheap = false, dairyFree = false, extendedIngredients = emptyList(),
            glutenFree = false, id = 7, image = "", readyInMinutes = 15, sourceName = null,
            sourceUrl = "", summary = "A soup recipe", title = "Carrot soup", vegan = true,
            vegetarian = true, veryHealthy = true
        ))
        val recipes = mutableStateOf(emptyList<FavoritesEntity>())
        val clicks = mutableListOf<Int>()
        val longPresses = mutableListOf<Int>()
        compose.setContent {
            MyFoodyTheme {
                FavoriteRecipesScreen(recipes.value, emptySet(), { clicks.add(it.id) }, { longPresses.add(it.id) })
            }
        }
        compose.onNodeWithText("No Favorites Recipes Yet").assertIsDisplayed()
        compose.runOnIdle { recipes.value = listOf(favorite) }
        compose.onNodeWithText("Carrot soup").performClick()
        compose.onNodeWithText("Carrot soup").performTouchInput { longClick() }
        compose.runOnIdle {
            assertEquals(listOf(42), clicks)
            assertEquals(listOf(42), longPresses)
        }
    }
}
