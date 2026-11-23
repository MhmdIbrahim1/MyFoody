package com.example.myfoody.presentation.foodjoke

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.model.FoodJoke
import com.example.myfoody.domain.repository.FoodJokeRepository
import com.example.myfoody.domain.usecase.foodjoke.FetchFoodJokeUseCase
import com.example.myfoody.domain.usecase.foodjoke.ObserveCachedFoodJokeUseCase
import com.example.myfoody.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoodJokeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeFoodJokeRepository : FoodJokeRepository {
        val cachedFlow = MutableStateFlow<FoodJoke?>(null)
        var nextFetchResult: AppResult<FoodJoke> = AppResult.Success(FoodJoke("Default joke"))
        var fetchCallCount = 0

        override fun observeCachedFoodJoke(): Flow<FoodJoke?> = cachedFlow

        override suspend fun fetchFoodJoke(): AppResult<FoodJoke> {
            fetchCallCount++
            val result = nextFetchResult
            if (result is AppResult.Success) {
                cachedFlow.value = result.data
            }
            return result
        }
    }

    private fun createViewModel(repository: FakeFoodJokeRepository): FoodJokeViewModel {
        return FoodJokeViewModel(
            observeCachedFoodJokeUseCase = ObserveCachedFoodJokeUseCase(repository),
            fetchFoodJokeUseCase = FetchFoodJokeUseCase(repository)
        )
    }

    @Test
    fun `loadFoodJoke emits Content on success and skips duplicate non-forced calls`() = runTest {
        val repo = FakeFoodJokeRepository().apply {
            nextFetchResult = AppResult.Success(FoodJoke("Fresh joke"))
        }
        val viewModel = createViewModel(repo)
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.loadFoodJoke()
        viewModel.loadFoodJoke()

        assertEquals(1, repo.fetchCallCount)
        assertEquals(FoodJokeUiState.Content("Fresh joke"), viewModel.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `loadFoodJoke falls back to cached joke on error and emits ShowToast`() = runTest {
        val repo = FakeFoodJokeRepository().apply {
            cachedFlow.value = FoodJoke("Cached joke")
            nextFetchResult = AppResult.Error(DataError.NoInternet)
        }
        val viewModel = createViewModel(repo)
        val events = mutableListOf<FoodJokeUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.loadFoodJoke()

        assertEquals(FoodJokeUiState.Content("Cached joke"), viewModel.uiState.value)
        assertEquals(listOf(FoodJokeUiEvent.ShowToast("No Internet Connection.")), events)

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `loadFoodJoke shows Error when cache is empty and updates to Content when cache arrives`() = runTest {
        val repo = FakeFoodJokeRepository().apply {
            nextFetchResult = AppResult.Error(DataError.ApiKeyLimited)
        }
        val viewModel = createViewModel(repo)
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.loadFoodJoke()
        assertEquals(FoodJokeUiState.Error("API Key Limited."), viewModel.uiState.value)

        repo.cachedFlow.value = FoodJoke("Late cached joke")
        assertEquals(FoodJokeUiState.Content("Late cached joke"), viewModel.uiState.value)

        stateJob.cancel()
    }

    @Test
    fun `onShareClicked emits current joke text or fallback text`() = runTest {
        val repo = FakeFoodJokeRepository().apply {
            nextFetchResult = AppResult.Success(FoodJoke("Shareable joke"))
        }
        val viewModel = createViewModel(repo)
        val events = mutableListOf<FoodJokeUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onShareClicked()
        viewModel.loadFoodJoke()
        viewModel.onShareClicked()

        assertEquals(
            listOf(
                FoodJokeUiEvent.ShareJoke(FoodJokeViewModel.DEFAULT_SHARE_TEXT),
                FoodJokeUiEvent.ShareJoke("Shareable joke")
            ),
            events
        )

        stateJob.cancel()
        eventJob.cancel()
    }
}
