package com.example.myfoody.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.usecase.network.ObserveBackOnlineUseCase
import com.example.myfoody.domain.usecase.network.ObserveNetworkStatusUseCase
import com.example.myfoody.domain.usecase.network.UpdateBackOnlineUseCase
import com.example.myfoody.domain.usecase.recipes.FetchRecipesUseCase
import com.example.myfoody.domain.usecase.recipes.ObserveCachedRecipesUseCase
import com.example.myfoody.domain.usecase.recipes.SearchRecipesUseCase
import com.example.myfoody.presentation.common.toDisplayMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val observeCachedRecipesUseCase: ObserveCachedRecipesUseCase,
    private val fetchRecipesUseCase: FetchRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    private val observeBackOnlineUseCase: ObserveBackOnlineUseCase,
    private val updateBackOnlineUseCase: UpdateBackOnlineUseCase
) : ViewModel() {

    private sealed interface RequestState {
        object Idle : RequestState
        object Loading : RequestState
        data class Success(val recipes: List<Recipe>) : RequestState
        data class Error(val message: String) : RequestState
    }

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    private val _uiEvent = MutableSharedFlow<RecipesUiEvent>(extraBufferCapacity = 2)
    val uiEvent: SharedFlow<RecipesUiEvent> = _uiEvent.asSharedFlow()

    private val cachedRecipesState: StateFlow<List<Recipe>?> = observeCachedRecipesUseCase()
        .map<List<Recipe>, List<Recipe>?> { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val uiState: StateFlow<RecipesUiState> = combine(
        cachedRecipesState,
        _requestState
    ) { cached, request ->
        when (request) {
            RequestState.Loading -> RecipesUiState.Loading
            is RequestState.Success -> RecipesUiState.Content(request.recipes)
            is RequestState.Error -> {
                if (!cached.isNullOrEmpty()) {
                    RecipesUiState.Content(cached)
                } else {
                    RecipesUiState.Error(request.message)
                }
            }
            RequestState.Idle -> {
                when {
                    cached == null -> RecipesUiState.Loading
                    cached.isNotEmpty() -> RecipesUiState.Content(cached)
                    else -> RecipesUiState.Loading
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipesUiState.Loading
    )

    private var monitoringJob: Job? = null
    private var fetchJob: Job? = null
    private var searchJob: Job? = null
    private var activeSearchQuery: String? = null
    private var pendingBackFromBottomSheet: Boolean = false
    private var isOnline: Boolean = false
    private var backOnline: Boolean = false
    private var lastConnectivity: Boolean? = null

    fun startObserving(backFromBottomSheet: Boolean = false) {
        if (backFromBottomSheet && monitoringJob == null) {
            pendingBackFromBottomSheet = true
        }
        if (monitoringJob != null) return

        monitoringJob = viewModelScope.launch {
            backOnline = observeBackOnlineUseCase().first()
            launch {
                observeBackOnlineUseCase().collect { value ->
                    backOnline = value
                }
            }
            launch {
                observeNetworkStatusUseCase().collect { connected ->
                    isOnline = connected
                    if (lastConnectivity != connected) {
                        handleConnectivityChanged(connected)
                        lastConnectivity = connected
                    }
                }
            }
        }
    }

    fun onSearchSubmitted(query: String) {
        activeSearchQuery = query
        fetchJob?.cancel()
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = searchRecipesUseCase(query)) {
                is AppResult.Success -> {
                    _requestState.value = RequestState.Success(result.data)
                }
                is AppResult.Error -> {
                    val message = result.error.toDisplayMessage()
                    _requestState.value = RequestState.Error(message)
                    _uiEvent.emit(RecipesUiEvent.ShowToast(message))
                }
            }
        }
    }

    fun onRecipeClicked(recipe: Recipe) {
        viewModelScope.launch {
            _uiEvent.emit(RecipesUiEvent.NavigateToDetails(recipe))
        }
    }

    fun onFiltersClicked() {
        viewModelScope.launch {
            if (isOnline) {
                _uiEvent.emit(RecipesUiEvent.NavigateToFilters)
            } else {
                _uiEvent.emit(RecipesUiEvent.ShowToast(NO_INTERNET_MESSAGE))
                updateBackOnlineUseCase(true)
            }
        }
    }

    private suspend fun handleConnectivityChanged(connected: Boolean) {
        if (!connected) {
            _uiEvent.emit(RecipesUiEvent.ShowToast(NO_INTERNET_MESSAGE))
            updateBackOnlineUseCase(true)
        } else if (backOnline) {
            _uiEvent.emit(RecipesUiEvent.ShowToast(BACK_ONLINE_MESSAGE))
            updateBackOnlineUseCase(false)
        }
        loadForConnectivity(connected)
    }

    private suspend fun loadForConnectivity(connected: Boolean) {
        val currentQuery = activeSearchQuery
        if (currentQuery != null) {
            if (connected && (_requestState.value is RequestState.Idle || _requestState.value is RequestState.Error)) {
                onSearchSubmitted(currentQuery)
            }
            return
        }
        val cached = observeCachedRecipesUseCase().first()
        if (activeSearchQuery != null) return
        if (!connected) {
            if (cached.isEmpty()) {
                _requestState.value = RequestState.Error(NO_INTERNET_MESSAGE)
            }
            return
        }
        val shouldFetch = cached.isEmpty() || pendingBackFromBottomSheet
        if (shouldFetch) {
            pendingBackFromBottomSheet = false
            fetchRecipes()
        }
    }

    private fun fetchRecipes() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = fetchRecipesUseCase()) {
                is AppResult.Success -> {
                    _requestState.value = RequestState.Success(result.data)
                }
                is AppResult.Error -> {
                    val message = result.error.toDisplayMessage()
                    _requestState.value = RequestState.Error(message)
                    _uiEvent.emit(RecipesUiEvent.ShowToast(message))
                }
            }
        }
    }

    companion object {
        const val NO_INTERNET_MESSAGE = "No Internet Connection."
        const val BACK_ONLINE_MESSAGE = "We're back online."
    }
}
