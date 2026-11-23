package com.example.myfoody.presentation.recipes

import com.example.myfoody.domain.model.Recipe

sealed interface RecipesUiState {
    object Loading : RecipesUiState
    data class Content(val recipes: List<Recipe>) : RecipesUiState
    data class Error(val message: String) : RecipesUiState
}
