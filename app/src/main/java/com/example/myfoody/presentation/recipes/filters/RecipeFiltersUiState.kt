package com.example.myfoody.presentation.recipes.filters

import com.example.myfoody.domain.model.MealAndDietFilter

data class RecipeFiltersUiState(
    val savedFilter: MealAndDietFilter? = null,
    val isApplying: Boolean = false
)
