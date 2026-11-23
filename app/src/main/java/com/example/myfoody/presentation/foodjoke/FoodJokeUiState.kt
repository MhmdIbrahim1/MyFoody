package com.example.myfoody.presentation.foodjoke

sealed interface FoodJokeUiState {
    object Loading : FoodJokeUiState
    data class Content(val joke: String) : FoodJokeUiState
    data class Error(val message: String) : FoodJokeUiState
}
