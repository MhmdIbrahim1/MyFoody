package com.example.myfoody.ui.fragments.recipes.bottomsheet

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.myfoody.R
import com.example.myfoody.date.MealAndDietType
import com.example.myfoody.ui.theme.MyFoodyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecipeFiltersScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun restoresFiltersByNameAndAppliesChanges() {
        val previous = MealAndDietType("salad", -1, "vegan", -1)
        var applied: MealAndDietType? = null
        compose.setContent {
            MyFoodyTheme { RecipeFiltersScreen(previous, false) { applied = it } }
        }
        compose.onNodeWithText("Salad").assertIsSelected()
        compose.onNodeWithText("Vegan").assertIsSelected()
        compose.onNodeWithText("Soup").performScrollTo().performClick()
        compose.onNodeWithText("Apply").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(MealAndDietType("soup", R.id.soup_chip, "vegan", R.id.vegan_chip), applied)
        }
    }
}
