package com.example.myfoody.presentation.recipes

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.NetworkMonitor
import com.example.myfoody.domain.repository.RecipesRepository
import com.example.myfoody.domain.repository.UserPreferencesRepository
import com.example.myfoody.domain.usecase.network.ObserveBackOnlineUseCase
import com.example.myfoody.domain.usecase.network.ObserveNetworkStatusUseCase
import com.example.myfoody.domain.usecase.network.UpdateBackOnlineUseCase
import com.example.myfoody.domain.usecase.recipes.FetchRecipesUseCase
import com.example.myfoody.domain.usecase.recipes.ObserveCachedRecipesUseCase
import com.example.myfoody.domain.usecase.recipes.SearchRecipesUseCase
import com.example.myfoody.testutil.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeRecipesRepository(
        initialCache: List<Recipe> = emptyList()
    ) : RecipesRepository {
        val cachedFlow = MutableStateFlow(initialCache)
        var fetchHandler: suspend (MealAndDietFilter) -> AppResult<List<Recipe>> = {
            AppResult.Success(emptyList())
        }
        var searchHandler: suspend (String) -> AppResult<List<Recipe>> = {
            AppResult.Success(emptyList())
        }
        var fetchCallCount = 0

        override fun observeCachedRecipes(): Flow<List<Recipe>> = cachedFlow

        override suspend fun fetchRecipes(filter: MealAndDietFilter): AppResult<List<Recipe>> {
            fetchCallCount++
            val result = fetchHandler(filter)
            if (result is AppResult.Success) {
                cachedFlow.value = result.data
            }
            return result
        }

        override suspend fun searchRecipes(query: String): AppResult<List<Recipe>> = searchHandler(query)
    }

    private class FakeUserPreferencesRepository(
        initialFilter: MealAndDietFilter = MealAndDietFilter.Default,
        initialBackOnline: Boolean = false
    ) : UserPreferencesRepository {
        val filterFlow = MutableStateFlow(initialFilter)
        val backOnlineFlow = MutableStateFlow(initialBackOnline)

        override fun observeMealAndDietFilter(): Flow<MealAndDietFilter> = filterFlow
        override suspend fun saveMealAndDietFilter(filter: MealAndDietFilter) {
            filterFlow.value = filter
        }

        override fun observeBackOnline(): Flow<Boolean> = backOnlineFlow
        override suspend fun saveBackOnline(backOnline: Boolean) {
            backOnlineFlow.value = backOnline
        }
    }

    private class FakeNetworkMonitor(initialOnline: Boolean = true) : NetworkMonitor {
        val onlineFlow = MutableStateFlow(initialOnline)
        override val isOnline: Flow<Boolean> = onlineFlow
        override fun isCurrentlyOnline(): Boolean = onlineFlow.value
    }

    private fun sampleRecipe(id: Int, title: String) = Recipe(
        id = id,
        title = title,
        summary = "Summary $id",
        image = "",
        aggregateLikes = 10,
        readyInMinutes = 20,
        sourceName = null,
        sourceUrl = "",
        cheap = true,
        dairyFree = true,
        glutenFree = true,
        vegan = true,
        vegetarian = true,
        veryHealthy = true,
        extendedIngredients = emptyList()
    )

    private fun createViewModel(
        recipesRepo: FakeRecipesRepository,
        prefsRepo: FakeUserPreferencesRepository,
        networkMonitor: FakeNetworkMonitor
    ): RecipesViewModel = RecipesViewModel(
        observeCachedRecipesUseCase = ObserveCachedRecipesUseCase(recipesRepo),
        fetchRecipesUseCase = FetchRecipesUseCase(recipesRepo, prefsRepo),
        searchRecipesUseCase = SearchRecipesUseCase(recipesRepo),
        observeNetworkStatusUseCase = ObserveNetworkStatusUseCase(networkMonitor),
        observeBackOnlineUseCase = ObserveBackOnlineUseCase(prefsRepo),
        updateBackOnlineUseCase = UpdateBackOnlineUseCase(prefsRepo)
    )

    @Test
    fun `uses cached recipes without fetching when cache is non-empty and not from bottom sheet`() = runTest {
        val cached = listOf(sampleRecipe(1, "Cached Pasta"))
        val recipesRepo = FakeRecipesRepository(initialCache = cached)
        val prefsRepo = FakeUserPreferencesRepository()
        val networkMonitor = FakeNetworkMonitor(initialOnline = true)
        val viewModel = createViewModel(recipesRepo, prefsRepo, networkMonitor)
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.startObserving(backFromBottomSheet = false)

        assertEquals(0, recipesRepo.fetchCallCount)
        assertEquals(RecipesUiState.Content(cached), viewModel.uiState.value)

        stateJob.cancel()
    }

    @Test
    fun `fetches from network when returning from bottom sheet even if cache exists`() = runTest {
        val cached = listOf(sampleRecipe(1, "Old Cached"))
        val fresh = listOf(sampleRecipe(2, "Fresh Salad"))
        val recipesRepo = FakeRecipesRepository(initialCache = cached).apply {
            fetchHandler = { AppResult.Success(fresh) }
        }
        val prefsRepo = FakeUserPreferencesRepository()
        val networkMonitor = FakeNetworkMonitor(initialOnline = true)
        val viewModel = createViewModel(recipesRepo, prefsRepo, networkMonitor)
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.startObserving(backFromBottomSheet = true)

        assertEquals(1, recipesRepo.fetchCallCount)
        assertEquals(RecipesUiState.Content(fresh), viewModel.uiState.value)

        stateJob.cancel()
    }

    @Test
    fun `submitting a new search cancels previous in-flight search`() = runTest {
        val firstSearchGate = CompletableDeferred<AppResult<List<Recipe>>>()
        val secondResult = listOf(sampleRecipe(9, "Fresh Soup"))
        var firstWasCancelled = false
        val recipesRepo = FakeRecipesRepository(initialCache = listOf(sampleRecipe(1, "Cached"))).apply {
            searchHandler = { query ->
                if (query == "first") {
                    try {
                        firstSearchGate.await()
                    } catch (cancelled: kotlinx.coroutines.CancellationException) {
                        firstWasCancelled = true
                        throw cancelled
                    }
                } else {
                    AppResult.Success(secondResult)
                }
            }
        }
        val viewModel = createViewModel(recipesRepo, FakeUserPreferencesRepository(), FakeNetworkMonitor(true))
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onSearchSubmitted("first")
        assertEquals(RecipesUiState.Loading, viewModel.uiState.value)

        viewModel.onSearchSubmitted("second")

        assertTrue(firstWasCancelled)
        assertEquals(RecipesUiState.Content(secondResult), viewModel.uiState.value)

        stateJob.cancel()
    }

    @Test
    fun `connectivity changes emit offline and back online toasts and update preference`() = runTest {
        val cached = listOf(sampleRecipe(1, "Cached"))
        val recipesRepo = FakeRecipesRepository(initialCache = cached)
        val prefsRepo = FakeUserPreferencesRepository(initialBackOnline = false)
        val networkMonitor = FakeNetworkMonitor(initialOnline = false)
        val viewModel = createViewModel(recipesRepo, prefsRepo, networkMonitor)
        val events = mutableListOf<RecipesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.startObserving()
        assertTrue(prefsRepo.backOnlineFlow.value)
        assertEquals(listOf(RecipesUiEvent.ShowToast(RecipesViewModel.NO_INTERNET_MESSAGE)), events)

        networkMonitor.onlineFlow.value = true
        assertFalse(prefsRepo.backOnlineFlow.value)
        assertEquals(
            listOf(
                RecipesUiEvent.ShowToast(RecipesViewModel.NO_INTERNET_MESSAGE),
                RecipesUiEvent.ShowToast(RecipesViewModel.BACK_ONLINE_MESSAGE)
            ),
            events
        )

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `onFiltersClicked navigates when online and shows toast when offline`() = runTest {
        val recipesRepo = FakeRecipesRepository(initialCache = listOf(sampleRecipe(1, "Cached")))
        val prefsRepo = FakeUserPreferencesRepository()
        val networkMonitor = FakeNetworkMonitor(initialOnline = true)
        val viewModel = createViewModel(recipesRepo, prefsRepo, networkMonitor)
        val events = mutableListOf<RecipesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.startObserving()
        viewModel.onFiltersClicked()
        assertEquals(listOf(RecipesUiEvent.NavigateToFilters), events)

        networkMonitor.onlineFlow.value = false
        events.clear()
        viewModel.onFiltersClicked()
        assertEquals(listOf(RecipesUiEvent.ShowToast(RecipesViewModel.NO_INTERNET_MESSAGE)), events)

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `fetch error shows Error state when cache is empty and emits ShowToast`() = runTest {
        val recipesRepo = FakeRecipesRepository(initialCache = emptyList()).apply {
            fetchHandler = { AppResult.Error(DataError.ApiKeyLimited) }
        }
        val viewModel = createViewModel(recipesRepo, FakeUserPreferencesRepository(), FakeNetworkMonitor(true))
        val events = mutableListOf<RecipesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.startObserving()

        assertEquals(RecipesUiState.Error("API Key Limited."), viewModel.uiState.value)
        assertEquals(listOf(RecipesUiEvent.ShowToast("API Key Limited.")), events)

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `offline start with empty cache shows single error toast and auto-fetches when back online`() = runTest {
        val fresh = listOf(sampleRecipe(7, "Online Pasta"))
        val recipesRepo = FakeRecipesRepository(initialCache = emptyList()).apply {
            fetchHandler = { AppResult.Success(fresh) }
        }
        val prefsRepo = FakeUserPreferencesRepository(initialBackOnline = false)
        val networkMonitor = FakeNetworkMonitor(initialOnline = false)
        val viewModel = createViewModel(recipesRepo, prefsRepo, networkMonitor)
        val events = mutableListOf<RecipesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.startObserving()

        assertEquals(0, recipesRepo.fetchCallCount)
        assertEquals(RecipesUiState.Error(RecipesViewModel.NO_INTERNET_MESSAGE), viewModel.uiState.value)
        assertEquals(listOf(RecipesUiEvent.ShowToast(RecipesViewModel.NO_INTERNET_MESSAGE)), events)

        networkMonitor.onlineFlow.value = true

        assertEquals(1, recipesRepo.fetchCallCount)
        assertEquals(RecipesUiState.Content(fresh), viewModel.uiState.value)
        assertEquals(
            listOf(
                RecipesUiEvent.ShowToast(RecipesViewModel.NO_INTERNET_MESSAGE),
                RecipesUiEvent.ShowToast(RecipesViewModel.BACK_ONLINE_MESSAGE)
            ),
            events
        )

        stateJob.cancel()
        eventJob.cancel()
    }
}
