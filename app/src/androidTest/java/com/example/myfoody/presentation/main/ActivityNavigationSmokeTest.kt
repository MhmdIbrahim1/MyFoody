package com.example.myfoody.presentation.main

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.myfoody.R
import com.example.myfoody.presentation.details.DetailsActivity
import com.example.myfoody.presentation.details.DetailsViewModel
import com.example.myfoody.presentation.navigation.IngredientParcelable
import com.example.myfoody.presentation.navigation.RecipeParcelable
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ActivityNavigationSmokeTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test fun bottomBarNavigatesToAllThreeDestinations() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            val destinations = listOf(
                "Favorites" to R.id.favoriteRecipesFragment,
                "Joke" to R.id.foodJokeFragment,
                "Recipes" to R.id.recipesFragment
            )
            for ((title, destination) in destinations) {
                compose.onNodeWithText(title).performClick()
                if (destination == R.id.favoriteRecipesFragment) scenario.recreate()
                compose.onNodeWithText(title).assertIsSelected()
                scenario.onActivity { activity ->
                    val host = activity.supportFragmentManager.fragments.filterIsInstance<NavHostFragment>().single()
                    assertEquals(destination, host.navController.currentDestination?.id)
                }
            }
        }
    }

    @Test fun detailsTabsFavoriteToggleAndUpWorkWithRecipeArgument() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recipe = RecipeParcelable(
            id = -kotlin.random.Random.nextInt(1, Int.MAX_VALUE),
            title = "Smoke recipe",
            summary = "Recipe summary",
            image = "",
            aggregateLikes = 3,
            readyInMinutes = 10,
            sourceName = "Smoke test",
            sourceUrl = "about:blank",
            cheap = true,
            dairyFree = true,
            glutenFree = true,
            vegan = true,
            vegetarian = true,
            veryHealthy = true,
            extendedIngredients = listOf(
                IngredientParcelable(1.0, "solid", "", "Smoke ingredient", "One cup of the ingredient", "cup")
            )
        )
        val intent = Intent(context, DetailsActivity::class.java).putExtra("result", recipe)
        ActivityScenario.launch<DetailsActivity>(intent).use { scenario ->
            compose.onNodeWithText("INGREDIENTS", ignoreCase = true).performClick()
            awaitText("Smoke ingredient")
            scenario.recreate()
            compose.onNodeWithText("INGREDIENTS", ignoreCase = true).assertIsSelected()
            awaitText("Smoke ingredient")
            compose.onNodeWithText("INSTRUCTIONS", ignoreCase = true).performClick()
            compose.onNodeWithText("OVERVIEW", ignoreCase = true).performClick()
            awaitText("Recipe summary")

            val saveLabel = context.getString(R.string.saved_favorite_menu)
            compose.onNodeWithContentDescription(saveLabel).performClick()
            awaitFavorite(scenario, true)
            compose.onNodeWithContentDescription(saveLabel).performClick()
            awaitFavorite(scenario, false)
            compose.onNodeWithContentDescription(
                context.getString(androidx.appcompat.R.string.abc_action_bar_up_description)
            ).performClick()
            compose.waitUntil(10_000) { scenario.state == Lifecycle.State.DESTROYED }
        }
    }

    private fun awaitText(text: String) {
        compose.waitUntil(10_000) {
            try {
                compose.onNodeWithText(text).assertIsDisplayed()
                true
            } catch (_: AssertionError) {
                false
            }
        }
    }

    private fun awaitFavorite(scenario: ActivityScenario<DetailsActivity>, expected: Boolean) {
        compose.waitUntil(10_000) {
            var matches = false
            scenario.onActivity { activity ->
                val state = ViewModelProvider(activity)[DetailsViewModel::class.java].uiState.value
                matches = state.isFavoriteActionEnabled && state.isFavorite == expected
            }
            matches
        }
    }
}
