package com.example.myfoody.presentation.recipes.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.usecase.filters.ObserveMealAndDietFilterUseCase
import com.example.myfoody.domain.usecase.filters.SaveMealAndDietFilterUseCase
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
class RecipeFiltersViewModel @Inject constructor(
    observeMealAndDietFilterUseCase: ObserveMealAndDietFilterUseCase,
    private val saveMealAndDietFilterUseCase: SaveMealAndDietFilterUseCase
) : ViewModel() {

    private val _isApplying = MutableStateFlow(false)
    private val _uiEvent = MutableSharedFlow<RecipeFiltersUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<RecipeFiltersUiEvent> = _uiEvent.asSharedFlow()

    val uiState: StateFlow<RecipeFiltersUiState> = combine(
        observeMealAndDietFilterUseCase(),
        _isApplying
    ) { filter, isApplying ->
        RecipeFiltersUiState(
            savedFilter = filter,
            isApplying = isApplying
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeFiltersUiState()
    )

    fun onApplyFilters(filter: MealAndDietFilter) {
        if (_isApplying.value) return
        _isApplying.value = true
        viewModelScope.launch {
            try {
                saveMealAndDietFilterUseCase(filter)
                _uiEvent.emit(RecipeFiltersUiEvent.FiltersApplied)
            } finally {
                _isApplying.value = false
            }
        }
    }
}
