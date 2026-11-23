package com.example.myfoody.presentation.recipes.filters

import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.repository.UserPreferencesRepository
import com.example.myfoody.domain.usecase.filters.ObserveMealAndDietFilterUseCase
import com.example.myfoody.domain.usecase.filters.SaveMealAndDietFilterUseCase
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
class RecipeFiltersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeUserPreferencesRepository(
        initialFilter: MealAndDietFilter = MealAndDietFilter.Default
    ) : UserPreferencesRepository {
        val filterFlow = MutableStateFlow(initialFilter)
        var saveGate: CompletableDeferred<Unit>? = null
        var saveCount = 0

        override fun observeMealAndDietFilter(): Flow<MealAndDietFilter> = filterFlow

        override suspend fun saveMealAndDietFilter(filter: MealAndDietFilter) {
            saveCount++
            saveGate?.await()
            filterFlow.value = filter
        }

        override fun observeBackOnline(): Flow<Boolean> = MutableStateFlow(false)
        override suspend fun saveBackOnline(backOnline: Boolean) = Unit
    }

    @Test
    fun `onApplyFilters saves filter emits FiltersApplied and ignores duplicate clicks while saving`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val repo = FakeUserPreferencesRepository().apply { saveGate = gate }
        val viewModel = RecipeFiltersViewModel(
            observeMealAndDietFilterUseCase = ObserveMealAndDietFilterUseCase(repo),
            saveMealAndDietFilterUseCase = SaveMealAndDietFilterUseCase(repo)
        )
        val events = mutableListOf<RecipeFiltersUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        assertEquals(MealAndDietFilter.Default, viewModel.uiState.value.savedFilter)
        assertFalse(viewModel.uiState.value.isApplying)

        val chosen = MealAndDietFilter("soup", 10, "vegan", 20)
        viewModel.onApplyFilters(chosen)
        assertTrue(viewModel.uiState.value.isApplying)

        viewModel.onApplyFilters(chosen)
        assertEquals(1, repo.saveCount)

        gate.complete(Unit)
        assertFalse(viewModel.uiState.value.isApplying)
        assertEquals(chosen, viewModel.uiState.value.savedFilter)
        assertEquals(listOf(RecipeFiltersUiEvent.FiltersApplied), events)

        stateJob.cancel()
        eventJob.cancel()
    }
}
