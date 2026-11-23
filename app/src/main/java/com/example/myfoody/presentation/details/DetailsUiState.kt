package com.example.myfoody.presentation.details

import com.example.myfoody.domain.model.Recipe

data class DetailsUiState(
    val recipe: Recipe? = null,
    val isFavorite: Boolean = false,
    val isFavoriteActionEnabled: Boolean = false
)
