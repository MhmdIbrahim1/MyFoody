package com.example.myfoody.presentation.details

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.FavoritesRepository
import com.example.myfoody.domain.usecase.favorites.ObserveFavoritesUseCase
import com.example.myfoody.domain.usecase.favorites.ToggleFavoriteRecipeUseCase
import com.example.myfoody.testutil.MainDispatcherRule
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
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeFavoritesRepository(
        private val autoEmitOnWrite: Boolean = true
    ) : FavoritesRepository {
        val flow = MutableStateFlow<List<FavoriteRecipe>>(emptyList())
        var addCount = 0
        var deleteCount = 0

        override fun observeFavorites(): Flow<List<FavoriteRecipe>> = flow

        override suspend fun addFavorite(recipe: Recipe) {
            addCount++
            if (autoEmitOnWrite) {
                val nextId = (flow.value.maxOfOrNull { it.id } ?: 0) + 1
                flow.value = flow.value + FavoriteRecipe(nextId, recipe)
            }
        }

        override suspend fun deleteFavorite(favoriteRecipe: FavoriteRecipe) {
            deleteCount++
            if (autoEmitOnWrite) {
                flow.value = flow.value.filterNot { it.id == favoriteRecipe.id }
            }
        }

        override suspend fun deleteFavorites(favoriteRecipes: List<FavoriteRecipe>) = Unit
        override suspend fun deleteAllFavorites() = Unit
    }

    private val sampleRecipe = Recipe(
        id = 77,
        title = "Lemon Pasta",
        summary = "Zesty pasta",
        image = "",
        aggregateLikes = 22,
        readyInMinutes = 18,
        sourceName = null,
        sourceUrl = "https://example.com/pasta",
        cheap = true,
        dairyFree = false,
        glutenFree = false,
        vegan = false,
        vegetarian = true,
        veryHealthy = true,
        extendedIngredients = emptyList()
    )

    private fun createViewModel(repo: FakeFavoritesRepository): DetailsViewModel = DetailsViewModel(
        observeFavoritesUseCase = ObserveFavoritesUseCase(repo),
        toggleFavoriteRecipeUseCase = ToggleFavoriteRecipeUseCase(repo)
    )

    @Test
    fun `toggle adds and removes favorite while emitting snackbar events`() = runTest {
        val repo = FakeFavoritesRepository()
        val viewModel = createViewModel(repo)
        val events = mutableListOf<DetailsUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.setRecipe(sampleRecipe)
        assertFalse(viewModel.uiState.value.isFavorite)
        assertTrue(viewModel.uiState.value.isFavoriteActionEnabled)

        viewModel.onToggleFavoriteClicked()
        assertTrue(viewModel.uiState.value.isFavorite)
        assertTrue(viewModel.uiState.value.isFavoriteActionEnabled)

        viewModel.onToggleFavoriteClicked()
        assertFalse(viewModel.uiState.value.isFavorite)
        assertTrue(viewModel.uiState.value.isFavoriteActionEnabled)

        assertEquals(
            listOf(
                DetailsUiEvent.ShowSnackbar("Recipe saved."),
                DetailsUiEvent.ShowSnackbar("Removed from Favorites.")
            ),
            events
        )

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `pending guard disables duplicate toggle until repository flow updates`() = runTest {
        val repo = FakeFavoritesRepository(autoEmitOnWrite = false)
        val viewModel = createViewModel(repo)
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.setRecipe(sampleRecipe)
        viewModel.onToggleFavoriteClicked()

        assertFalse(viewModel.uiState.value.isFavoriteActionEnabled)
        viewModel.onToggleFavoriteClicked()
        assertEquals(1, repo.addCount)

        repo.flow.value = listOf(FavoriteRecipe(1, sampleRecipe))
        assertTrue(viewModel.uiState.value.isFavorite)
        assertTrue(viewModel.uiState.value.isFavoriteActionEnabled)

        stateJob.cancel()
    }
}
