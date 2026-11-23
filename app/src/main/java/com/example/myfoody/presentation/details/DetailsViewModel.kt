package com.example.myfoody.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.usecase.favorites.ObserveFavoritesUseCase
import com.example.myfoody.domain.usecase.favorites.ToggleFavoriteRecipeUseCase
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
class DetailsViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    private val _pendingFavorite = MutableStateFlow<Boolean?>(null)
    private val _uiEvent = MutableSharedFlow<DetailsUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<DetailsUiEvent> = _uiEvent.asSharedFlow()

    private val favoritesState: StateFlow<List<FavoriteRecipe>?> = observeFavoritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val uiState: StateFlow<DetailsUiState> = combine(
        _recipe,
        favoritesState,
        _pendingFavorite
    ) { recipe, favorites, pendingFavorite ->
        val savedFavorite = if (recipe != null && favorites != null) {
            favorites.firstOrNull { it.recipe.id == recipe.id }
        } else {
            null
        }
        val isSaved = savedFavorite != null
        val isPending = pendingFavorite != null && pendingFavorite != isSaved
        DetailsUiState(
            recipe = recipe,
            isFavorite = isSaved,
            isFavoriteActionEnabled = recipe != null && favorites != null && !isPending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailsUiState()
    )

    fun setRecipe(recipe: Recipe) {
        if (_recipe.value != recipe) {
            _recipe.value = recipe
        }
    }

    fun onToggleFavoriteClicked() {
        val recipe = _recipe.value ?: return
        val favorites = favoritesState.value ?: return
        val existingFavorite = favorites.firstOrNull { it.recipe.id == recipe.id }
        val isCurrentlySaved = existingFavorite != null
        val pending = _pendingFavorite.value
        if (pending != null && pending != isCurrentlySaved) return

        val expectedSaved = !isCurrentlySaved
        _pendingFavorite.value = expectedSaved
        viewModelScope.launch {
            try {
                val added = toggleFavoriteRecipeUseCase(recipe, existingFavorite)
                val message = if (added) "Recipe saved." else "Removed from Favorites."
                _uiEvent.emit(DetailsUiEvent.ShowSnackbar(message))
            } catch (exception: Exception) {
                _pendingFavorite.value = null
                throw exception
            }
        }
    }
}
