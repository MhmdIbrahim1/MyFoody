package com.example.myfoody.presentation.foodjoke

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.myfoody.presentation.theme.MyFoodyTheme
import org.junit.Rule
import org.junit.Test

class FoodJokeScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun errorIsReplacedByTheCachedJoke() {
        val uiState = mutableStateOf<FoodJokeUiState>(
            FoodJokeUiState.Error("No Internet Connection.")
        )
        compose.setContent {
            MyFoodyTheme { FoodJokeScreen(uiState.value) }
        }
        compose.onNodeWithText("No Internet Connection.").assertIsDisplayed()
        compose.runOnIdle { uiState.value = FoodJokeUiState.Content("Saved joke") }
        compose.onNodeWithText("Saved joke").assertIsDisplayed()
        compose.onNodeWithText("No Internet Connection.").assertDoesNotExist()
    }
}
