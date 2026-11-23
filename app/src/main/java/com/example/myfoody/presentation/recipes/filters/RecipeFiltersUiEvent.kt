package com.example.myfoody.presentation.recipes.filters

sealed interface RecipeFiltersUiEvent {
    object FiltersApplied : RecipeFiltersUiEvent
}
