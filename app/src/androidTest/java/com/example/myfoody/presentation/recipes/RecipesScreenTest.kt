package com.example.myfoody.presentation.recipes

import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.view.descendants
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myfoody.R
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.presentation.theme.MyFoodyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipesScreenTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingErrorAndCachedRecipeExposeTheirRespectiveActions() {
        val recipe = Recipe(
            id = 13,
            title = "Roasted carrots",
            summary = "Summary",
            image = "",
            aggregateLikes = 14,
            readyInMinutes = 25,
            sourceName = null,
            sourceUrl = "",
            cheap = false,
            dairyFree = false,
            glutenFree = false,
            vegan = false,
            vegetarian = true,
            veryHealthy = false,
            extendedIngredients = emptyList()
        )
        val uiState = mutableStateOf<RecipesUiState>(RecipesUiState.Loading)
        var selected: Recipe? = null
        var filtered = false
        compose.setContent {
            MyFoodyTheme {
                RecipesScreen(
                    uiState = uiState.value,
                    openRecipe = { selected = it },
                    openFilters = { filtered = true }
                )
            }
        }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.runOnIdle { uiState.value = RecipesUiState.Error("Offline") }
        compose.onNodeWithText("Offline").assertIsDisplayed()
        compose.onNodeWithText("Roasted carrots").assertDoesNotExist()
        compose.runOnIdle { uiState.value = RecipesUiState.Content(listOf(recipe)) }
        compose.waitForIdle()
        val imageRight = compose.runOnIdle {
            val decor = compose.activity.window.decorView as ViewGroup
            val image = decor.descendants.filterIsInstance<ImageView>().single { it.tag == recipe.image }
            val location = IntArray(2)
            image.getLocationInWindow(location)
            assertTrue(image.width > 0 && image.height > 0)
            location[0] + image.width
        }
        listOf(recipe.title, recipe.summary, recipe.aggregateLikes.toString()).forEach { label ->
            val bounds = compose.onNodeWithText(label, useUnmergedTree = true)
                .fetchSemanticsNode().boundsInWindow
            assertTrue("$label overlaps the recipe image", bounds.left >= imageRight)
        }
        compose.onNodeWithText("Roasted carrots").performClick()
        val filterLabel = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.meal_type)
        compose.onNodeWithContentDescription(filterLabel).performClick()
        compose.runOnIdle {
            assertEquals(recipe, selected)
            assertTrue(filtered)
        }
    }
}
