package com.example.myfoody.presentation.foodjoke

sealed interface FoodJokeUiEvent {
    data class ShowToast(val message: String) : FoodJokeUiEvent
    data class ShareJoke(val text: String) : FoodJokeUiEvent
}
