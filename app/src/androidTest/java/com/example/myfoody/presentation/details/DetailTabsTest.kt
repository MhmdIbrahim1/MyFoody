package com.example.myfoody.presentation.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.presentation.theme.MyFoodyTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailTabsTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun ingredientsScrollAndShowAllFieldsForTheSelectedRow() {
        val ingredients = List(20) { index ->
            Ingredient(index.toDouble(), "solid", "", "ingredient $index", "chopped $index", "grams")
        }
        compose.setContent { MyFoodyTheme { IngredientsScreen(ingredients) } }
        compose.onNodeWithText("Ingredient 0").assertIsDisplayed()
        compose.onNode(hasScrollAction()).performScrollToIndex(19)
        compose.onNodeWithText("Ingredient 19").assertIsDisplayed()
        compose.onNodeWithText("19.0").assertIsDisplayed()
        compose.onNodeWithText("chopped 19").assertIsDisplayed()
    }

    @Test
    fun overviewHeaderCollapsesAndExpandsWhenSwipingTheSummary() {
        compose.setContent { MyFoodyTheme { OverviewScreen(null) } }
        val title = compose.onNodeWithText("Title")
        val expandedTop = title.fetchSemanticsNode().boundsInRoot.top
        val summary = compose.onNodeWithText("TextView")
        summary.performTouchInput { swipeUp() }
        compose.waitForIdle()
        val collapsedTop = title.fetchSemanticsNode().boundsInRoot.top
        assertTrue(collapsedTop < expandedTop)
        summary.performTouchInput { swipeDown() }
        compose.waitForIdle()
        assertTrue(title.fetchSemanticsNode().boundsInRoot.top > collapsedTop)
    }
}
