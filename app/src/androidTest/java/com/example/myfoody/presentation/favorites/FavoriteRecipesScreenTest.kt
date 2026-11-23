package com.example.myfoody.presentation.favorites

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.presentation.theme.MyFoodyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoriteRecipesScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun showsEmptyStateAndDispatchesDistinctTapAndLongPressEvents() {
        val favorite = FavoriteRecipe(
            id = 42,
            recipe = Recipe(
                id = 7,
                title = "Carrot soup",
                summary = "A soup recipe",
                image = "",
                aggregateLikes = 0,
                readyInMinutes = 15,
                sourceName = null,
                sourceUrl = "",
                cheap = false,
                dairyFree = false,
                glutenFree = false,
                vegan = true,
                vegetarian = true,
                veryHealthy = true,
                extendedIngredients = emptyList()
            )
        )
        val uiState = mutableStateOf(FavoritesUiState())
        val clicks = mutableListOf<Int>()
        val longPresses = mutableListOf<Int>()
        compose.setContent {
            MyFoodyTheme {
                FavoriteRecipesScreen(
                    uiState = uiState.value,
                    onClick = { clicks.add(it.id) },
                    onLongClick = { longPresses.add(it.id) }
                )
            }
        }
        compose.onNodeWithText("No Favorites Recipes Yet").assertIsDisplayed()
        compose.runOnIdle { uiState.value = FavoritesUiState(favorites = listOf(favorite)) }
        compose.onNodeWithText("Carrot soup").performClick()
        compose.onNodeWithText("Carrot soup").performTouchInput { longClick() }
        compose.runOnIdle {
            assertEquals(listOf(42), clicks)
            assertEquals(listOf(42), longPresses)
        }
    }
}
