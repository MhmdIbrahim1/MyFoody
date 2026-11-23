package com.example.myfoody.presentation.favorites

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.FavoritesRepository
import com.example.myfoody.domain.usecase.favorites.DeleteAllFavoritesUseCase
import com.example.myfoody.domain.usecase.favorites.DeleteFavoriteRecipesUseCase
import com.example.myfoody.domain.usecase.favorites.ObserveFavoritesUseCase
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
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeFavoritesRepository(
        initial: List<FavoriteRecipe> = emptyList()
    ) : FavoritesRepository {
        val flow = MutableStateFlow(initial)

        override fun observeFavorites(): Flow<List<FavoriteRecipe>> = flow

        override suspend fun addFavorite(recipe: Recipe) {
            val nextId = (flow.value.maxOfOrNull { it.id } ?: 0) + 1
            flow.value = flow.value + FavoriteRecipe(nextId, recipe)
        }

        override suspend fun deleteFavorite(favoriteRecipe: FavoriteRecipe) {
            flow.value = flow.value.filterNot { it.id == favoriteRecipe.id }
        }

        override suspend fun deleteFavorites(favoriteRecipes: List<FavoriteRecipe>) {
            val ids = favoriteRecipes.map { it.id }.toSet()
            flow.value = flow.value.filterNot { it.id in ids }
        }

        override suspend fun deleteAllFavorites() {
            flow.value = emptyList()
        }
    }

    private fun favorite(favId: Int, recipeId: Int) = FavoriteRecipe(
        id = favId,
        recipe = Recipe(
            id = recipeId,
            title = "Recipe $recipeId",
            summary = "Summary",
            image = "",
            aggregateLikes = 5,
            readyInMinutes = 10,
            sourceName = null,
            sourceUrl = "",
            cheap = false,
            dairyFree = false,
            glutenFree = false,
            vegan = false,
            vegetarian = false,
            veryHealthy = false,
            extendedIngredients = emptyList()
        )
    )

    private fun createViewModel(repo: FakeFavoritesRepository): FavoritesViewModel = FavoritesViewModel(
        observeFavoritesUseCase = ObserveFavoritesUseCase(repo),
        deleteFavoriteRecipesUseCase = DeleteFavoriteRecipesUseCase(repo),
        deleteAllFavoritesUseCase = DeleteAllFavoritesUseCase(repo)
    )

    @Test
    fun `clicking favorite outside selection mode emits NavigateToDetails`() = runTest {
        val fav = favorite(1, 100)
        val repo = FakeFavoritesRepository(listOf(fav))
        val viewModel = createViewModel(repo)
        val events = mutableListOf<FavoritesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onFavoriteClicked(fav)

        assertEquals(listOf(FavoritesUiEvent.NavigateToDetails(fav.recipe)), events)

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `long click starts selection mode and deleteSelected removes items with snackbar`() = runTest {
        val fav1 = favorite(1, 101)
        val fav2 = favorite(2, 102)
        val fav3 = favorite(3, 103)
        val repo = FakeFavoritesRepository(listOf(fav1, fav2, fav3))
        val viewModel = createViewModel(repo)
        val events = mutableListOf<FavoritesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onFavoriteLongClicked(fav1)
        assertTrue(viewModel.uiState.value.isSelectionMode)
        assertEquals("1 item selected", viewModel.uiState.value.selectionTitle)

        viewModel.onFavoriteClicked(fav2)
        assertEquals(setOf(1, 2), viewModel.uiState.value.selectedIds)
        assertEquals("2 items selected", viewModel.uiState.value.selectionTitle)

        viewModel.onDeleteSelectedClicked()

        assertFalse(viewModel.uiState.value.isSelectionMode)
        assertEquals(listOf(fav3), viewModel.uiState.value.favorites)
        assertEquals(listOf(FavoritesUiEvent.ShowSnackbar("2 Recipe/s removed.")), events)

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `onDeleteAllClicked clears all favorites and emits snackbar`() = runTest {
        val repo = FakeFavoritesRepository(listOf(favorite(1, 101), favorite(2, 102)))
        val viewModel = createViewModel(repo)
        val events = mutableListOf<FavoritesUiEvent>()
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val eventJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onDeleteAllClicked()

        assertTrue(viewModel.uiState.value.favorites.isEmpty())
        assertEquals(listOf(FavoritesUiEvent.ShowSnackbar("All recipes removed.")), events)

        stateJob.cancel()
        eventJob.cancel()
    }

    @Test
    fun `external removal prunes stale selected ids automatically`() = runTest {
        val fav1 = favorite(1, 101)
        val fav2 = favorite(2, 102)
        val repo = FakeFavoritesRepository(listOf(fav1, fav2))
        val viewModel = createViewModel(repo)
        val stateJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onFavoriteLongClicked(fav1)
        assertEquals(setOf(1), viewModel.uiState.value.selectedIds)

        repo.flow.value = listOf(fav2)
        assertFalse(viewModel.uiState.value.isSelectionMode)
        assertTrue(viewModel.uiState.value.selectedIds.isEmpty())

        stateJob.cancel()
    }
}
