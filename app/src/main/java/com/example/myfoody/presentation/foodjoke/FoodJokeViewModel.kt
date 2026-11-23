package com.example.myfoody.presentation.foodjoke

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.usecase.foodjoke.FetchFoodJokeUseCase
import com.example.myfoody.domain.usecase.foodjoke.ObserveCachedFoodJokeUseCase
import com.example.myfoody.presentation.common.toDisplayMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodJokeViewModel @Inject constructor(
    observeCachedFoodJokeUseCase: ObserveCachedFoodJokeUseCase,
    private val fetchFoodJokeUseCase: FetchFoodJokeUseCase
) : ViewModel() {

    private sealed interface FetchState {
        object Loading : FetchState
        data class Success(val text: String) : FetchState
        data class Error(val message: String) : FetchState
    }

    private val _fetchState = MutableStateFlow<FetchState>(FetchState.Loading)
    private val _uiEvent = MutableSharedFlow<FoodJokeUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<FoodJokeUiEvent> = _uiEvent.asSharedFlow()

    val uiState: StateFlow<FoodJokeUiState> = combine(
        _fetchState,
        observeCachedFoodJokeUseCase()
    ) { fetchState, cachedJoke ->
        when (fetchState) {
            FetchState.Loading -> FoodJokeUiState.Loading
            is FetchState.Success -> FoodJokeUiState.Content(fetchState.text)
            is FetchState.Error -> {
                if (cachedJoke != null) {
                    FoodJokeUiState.Content(cachedJoke.text)
                } else {
                    FoodJokeUiState.Error(fetchState.message)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FoodJokeUiState.Loading
    )

    private var hasLoaded = false

    fun loadFoodJoke(forceRefresh: Boolean = false) {
        if (!forceRefresh && hasLoaded) return
        hasLoaded = true
        viewModelScope.launch {
            _fetchState.value = FetchState.Loading
            when (val result = fetchFoodJokeUseCase()) {
                is AppResult.Success -> {
                    _fetchState.value = FetchState.Success(result.data.text)
                }
                is AppResult.Error -> {
                    val message = result.error.toDisplayMessage(notFoundMessage = "Food Joke Not Found.")
                    _fetchState.value = FetchState.Error(message)
                    _uiEvent.emit(FoodJokeUiEvent.ShowToast(message))
                }
            }
        }
    }

    fun onShareClicked() {
        val text = (uiState.value as? FoodJokeUiState.Content)?.joke ?: DEFAULT_SHARE_TEXT
        viewModelScope.launch {
            _uiEvent.emit(FoodJokeUiEvent.ShareJoke(text))
        }
    }

    companion object {
        const val DEFAULT_SHARE_TEXT = "No Food Joke"
    }
}
