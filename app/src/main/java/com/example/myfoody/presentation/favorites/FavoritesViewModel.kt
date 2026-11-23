package com.example.myfoody.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.usecase.favorites.DeleteAllFavoritesUseCase
import com.example.myfoody.domain.usecase.favorites.DeleteFavoriteRecipesUseCase
import com.example.myfoody.domain.usecase.favorites.ObserveFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val deleteFavoriteRecipesUseCase: DeleteFavoriteRecipesUseCase,
    private val deleteAllFavoritesUseCase: DeleteAllFavoritesUseCase
) : ViewModel() {

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _uiEvent = MutableSharedFlow<FavoritesUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<FavoritesUiEvent> = _uiEvent.asSharedFlow()

    val uiState: StateFlow<FavoritesUiState> = combine(
        observeFavoritesUseCase(),
        _selectedIds
    ) { favorites, selectedIds ->
        val validIds = favorites.map { it.id }.toSet()
        FavoritesUiState(
            favorites = favorites,
            selectedIds = selectedIds.intersect(validIds)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavoritesUiState()
    )

    fun onFavoriteClicked(favorite: FavoriteRecipe) {
        if (uiState.value.isSelectionMode) {
            _selectedIds.update { current ->
                if (favorite.id in current) current - favorite.id else current + favorite.id
            }
        } else {
            viewModelScope.launch {
                _uiEvent.emit(FavoritesUiEvent.NavigateToDetails(favorite.recipe))
            }
        }
    }

    fun onFavoriteLongClicked(favorite: FavoriteRecipe) {
        if (!uiState.value.isSelectionMode) {
            _selectedIds.value = setOf(favorite.id)
        }
    }

    fun onDeleteSelectedClicked() {
        val currentState = uiState.value
        val selected = currentState.favorites.filter { it.id in currentState.selectedIds }
        if (selected.isEmpty()) return
        _selectedIds.value = emptySet()
        viewModelScope.launch {
            deleteFavoriteRecipesUseCase(selected)
            _uiEvent.emit(FavoritesUiEvent.ShowSnackbar("${selected.size} Recipe/s removed."))
        }
    }

    fun onDeleteAllClicked() {
        _selectedIds.value = emptySet()
        viewModelScope.launch {
            deleteAllFavoritesUseCase()
            _uiEvent.emit(FavoritesUiEvent.ShowSnackbar("All recipes removed."))
        }
    }

    fun onClearSelection() {
        if (_selectedIds.value.isNotEmpty()) {
            _selectedIds.value = emptySet()
        }
    }
}
