package com.example.myfoody.ui.fragments.foodJoke

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.myfoody.ui.theme.MyFoodyTheme
import org.junit.Rule
import org.junit.Test

class FoodJokeScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun errorIsReplacedByTheCachedJoke() {
        val cached = mutableStateOf<String?>(null)
        compose.setContent {
            MyFoodyTheme { FoodJokeScreen(cached.value, false, "No Internet Connection.") }
        }
        compose.onNodeWithText("No Internet Connection.").assertIsDisplayed()
        compose.runOnIdle { cached.value = "Saved joke" }
        compose.onNodeWithText("Saved joke").assertIsDisplayed()
        compose.onNodeWithText("No Internet Connection.").assertDoesNotExist()
    }
}
