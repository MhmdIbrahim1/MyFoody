package com.example.myfoody.presentation.recipes.filters

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.myfoody.R
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.presentation.theme.MyFoodyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecipeFiltersScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun restoresFiltersByNameAndAppliesChanges() {
        val previous = MealAndDietFilter("salad", -1, "vegan", -1)
        var applied: MealAndDietFilter? = null
        compose.setContent {
            MyFoodyTheme { RecipeFiltersScreen(previous, false) { applied = it } }
        }
        compose.onNodeWithText("Salad").assertIsSelected()
        compose.onNodeWithText("Vegan").assertIsSelected()
        compose.onNodeWithText("Soup").performScrollTo().performClick()
        compose.onNodeWithText("Apply").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(MealAndDietFilter("soup", R.id.soup_chip, "vegan", R.id.vegan_chip), applied)
        }
    }
}
