package com.example.myfoody.data.repository

import com.example.myfoody.data.local.database.entity.FavoritesEntity
import com.example.myfoody.data.local.database.entity.FoodJokeEntity
import com.example.myfoody.data.local.database.entity.RecipesEntity
import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.data.remote.datasource.RecipesRemoteDataSource
import com.example.myfoody.data.remote.dto.FoodJokeDto
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import com.example.myfoody.data.remote.util.SafeApiCall
import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.model.FoodJoke
import com.example.myfoody.domain.repository.NetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FoodJokeRepositoryImplTest {

    private class FakeNetworkMonitor(var online: Boolean = true) : NetworkMonitor {
        override val isOnline: Flow<Boolean> = flowOf(online)
        override fun isCurrentlyOnline(): Boolean = online
    }

    private class FakeRemoteDataSource(
        var foodJokeResponse: () -> Response<FoodJokeDto>
    ) : RecipesRemoteDataSource {
        override suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipeResponseDto> {
            throw UnsupportedOperationException()
        }

        override suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipeResponseDto> {
            throw UnsupportedOperationException()
        }

        override suspend fun getFoodJoke(apiKey: String): Response<FoodJokeDto> = foodJokeResponse()
    }

    private class FakeLocalDataSource : RecipesLocalDataSource {
        val foodJokesFlow = MutableStateFlow<List<FoodJokeEntity>>(emptyList())
        val insertedFoodJokes = mutableListOf<FoodJokeEntity>()

        override fun readRecipes(): Flow<List<RecipesEntity>> = flowOf(emptyList())
        override suspend fun insertRecipes(recipesEntity: RecipesEntity) = Unit
        override fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> = flowOf(emptyList())
        override suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) = Unit
        override suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) = Unit
        override suspend fun deleteFavoriteRecipes(favoritesEntities: List<FavoritesEntity>) = Unit
        override suspend fun deleteAllFavoriteRecipes() = Unit

        override fun readFoodJoke(): Flow<List<FoodJokeEntity>> = foodJokesFlow

        override suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
            insertedFoodJokes.add(foodJokeEntity)
            foodJokesFlow.value = listOf(foodJokeEntity)
        }
    }

    @Test
    fun `fetchFoodJoke caches joke in local data source when API succeeds`() = runTest {
        val local = FakeLocalDataSource()
        val remote = FakeRemoteDataSource { Response.success(FoodJokeDto("A spicy joke")) }
        val repository = FoodJokeRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val result = repository.fetchFoodJoke()

        assertEquals(AppResult.Success(FoodJoke("A spicy joke")), result)
        assertEquals(1, local.insertedFoodJokes.size)
        assertEquals("A spicy joke", local.insertedFoodJokes.single().foodJoke.text)
        assertEquals(FoodJoke("A spicy joke"), repository.observeCachedFoodJoke().first())
    }

    @Test
    fun `fetchFoodJoke returns NoInternet and leaves cache untouched when offline`() = runTest {
        val local = FakeLocalDataSource()
        val existing = FoodJokeEntity(FoodJokeDto("Cached joke"))
        local.foodJokesFlow.value = listOf(existing)
        val remote = FakeRemoteDataSource { Response.success(FoodJokeDto("Fresh joke")) }
        val repository = FoodJokeRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = false)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val result = repository.fetchFoodJoke()

        assertEquals(AppResult.Error(DataError.NoInternet), result)
        assertEquals(0, local.insertedFoodJokes.size)
        assertEquals(FoodJoke("Cached joke"), repository.observeCachedFoodJoke().first())
    }

    @Test
    fun `fetchFoodJoke returns ApiKeyLimited on 402 without caching`() = runTest {
        val local = FakeLocalDataSource()
        val remote = FakeRemoteDataSource { Response.error(402, ResponseBody.create(null, "")) }
        val repository = FoodJokeRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val result = repository.fetchFoodJoke()

        assertEquals(AppResult.Error(DataError.ApiKeyLimited), result)
        assertEquals(0, local.insertedFoodJokes.size)
        assertNull(repository.observeCachedFoodJoke().first())
    }
}
