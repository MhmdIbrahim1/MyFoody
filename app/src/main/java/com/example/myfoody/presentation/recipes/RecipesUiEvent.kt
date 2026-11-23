package com.example.myfoody.presentation.recipes

import com.example.myfoody.domain.model.Recipe

sealed interface RecipesUiEvent {
    data class ShowToast(val message: String) : RecipesUiEvent
    data class NavigateToDetails(val recipe: Recipe) : RecipesUiEvent
    object NavigateToFilters : RecipesUiEvent
}
