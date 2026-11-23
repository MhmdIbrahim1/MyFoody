package com.example.myfoody.ui.fragments.recipes

import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.descendants
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myfoody.R
import com.example.myfoody.models.Result
import com.example.myfoody.ui.theme.MyFoodyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipesScreenTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingErrorAndCachedRecipeExposeTheirRespectiveActions() {
        val recipe = Result(
            14, false, false, emptyList(), false, 13, "", 25, null, "", "Summary", "Roasted carrots",
            false, true, false
        )
        val loading = mutableStateOf(true)
        val error = mutableStateOf<String?>("Offline")
        var selected: Result? = null
        var filtered = false
        compose.setContent {
            MyFoodyTheme {
                RecipesScreen(listOf(recipe), loading.value, error.value, { selected = it }, { filtered = true })
            }
        }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.runOnIdle { loading.value = false }
        compose.onNodeWithText("Offline").assertIsDisplayed()
        compose.onNodeWithText("Roasted carrots").assertDoesNotExist()
        compose.runOnIdle { error.value = null }
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
