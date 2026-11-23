package com.example.myfoody.presentation.favorites

import com.example.myfoody.domain.model.Recipe

sealed interface FavoritesUiEvent {
    data class NavigateToDetails(val recipe: Recipe) : FavoritesUiEvent
    data class ShowSnackbar(val message: String) : FavoritesUiEvent
}
