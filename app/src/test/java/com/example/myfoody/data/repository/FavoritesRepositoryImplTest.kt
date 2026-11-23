package com.example.myfoody.data.repository

import com.example.myfoody.data.local.database.entity.FavoritesEntity
import com.example.myfoody.data.local.database.entity.FoodJokeEntity
import com.example.myfoody.data.local.database.entity.RecipesEntity
import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.domain.model.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesRepositoryImplTest {

    private class FakeLocalDataSource : RecipesLocalDataSource {
        private var nextId = 1
        val favoritesFlow = MutableStateFlow<List<FavoritesEntity>>(emptyList())

        override fun readRecipes(): Flow<List<RecipesEntity>> = flowOf(emptyList())
        override suspend fun insertRecipes(recipesEntity: RecipesEntity) = Unit
        override fun readFoodJoke(): Flow<List<FoodJokeEntity>> = flowOf(emptyList())
        override suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) = Unit

        override fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> = favoritesFlow

        override suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
            val assignedId = if (favoritesEntity.id == 0) nextId++ else favoritesEntity.id
            favoritesFlow.value = favoritesFlow.value + favoritesEntity.copy(id = assignedId)
        }

        override suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
            favoritesFlow.value = favoritesFlow.value.filterNot { it.id == favoritesEntity.id }
        }

        override suspend fun deleteFavoriteRecipes(favoritesEntities: List<FavoritesEntity>) {
            val idsToDelete = favoritesEntities.map { it.id }.toSet()
            favoritesFlow.value = favoritesFlow.value.filterNot { it.id in idsToDelete }
        }

        override suspend fun deleteAllFavoriteRecipes() {
            favoritesFlow.value = emptyList()
        }
    }

    private fun sampleRecipe(id: Int, title: String = "Recipe $id") = Recipe(
        id = id,
        title = title,
        summary = "Summary $id",
        image = "",
        aggregateLikes = 10,
        readyInMinutes = 15,
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

    @Test
    fun `add delete batch-delete and clearAll favorites update observed domain flow`() = runTest {
        val local = FakeLocalDataSource()
        val repository = FavoritesRepositoryImpl(
            localDataSource = local,
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        repository.addFavorite(sampleRecipe(10))
        repository.addFavorite(sampleRecipe(20))
        repository.addFavorite(sampleRecipe(30))

        val initial = repository.observeFavorites().first()
        assertEquals(listOf(10, 20, 30), initial.map { it.recipe.id })

        repository.deleteFavorite(initial.first())
        val afterSingleDelete = repository.observeFavorites().first()
        assertEquals(listOf(20, 30), afterSingleDelete.map { it.recipe.id })

        repository.deleteFavorites(listOf(afterSingleDelete.first()))
        val afterBatchDelete = repository.observeFavorites().first()
        assertEquals(listOf(30), afterBatchDelete.map { it.recipe.id })

        repository.deleteAllFavorites()
        assertTrue(repository.observeFavorites().first().isEmpty())
    }
}
