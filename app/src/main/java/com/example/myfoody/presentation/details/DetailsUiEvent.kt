package com.example.myfoody.presentation.details

sealed interface DetailsUiEvent {
    data class ShowSnackbar(val message: String) : DetailsUiEvent
}
