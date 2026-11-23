package com.example.myfoody.data.repository

import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.local.database.entity.FavoritesEntity
import com.example.myfoody.data.local.database.entity.FoodJokeEntity
import com.example.myfoody.data.local.database.entity.RecipesEntity
import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.data.remote.datasource.RecipesRemoteDataSource
import com.example.myfoody.data.remote.dto.FoodJokeDto
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import com.example.myfoody.data.remote.dto.RecipeDto
import com.example.myfoody.data.remote.util.SafeApiCall
import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.repository.NetworkMonitor
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
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesRepositoryImplTest {

    private class FakeNetworkMonitor(var online: Boolean = true) : NetworkMonitor {
        override val isOnline: Flow<Boolean> = flowOf(online)
        override fun isCurrentlyOnline(): Boolean = online
    }

    private class FakeRemoteDataSource : RecipesRemoteDataSource {
        var lastGetQueries: Map<String, String>? = null
        var lastSearchQueries: Map<String, String>? = null
        var getRecipesResponse: () -> Response<FoodRecipeResponseDto> = {
            Response.success(FoodRecipeResponseDto(emptyList()))
        }
        var searchRecipesResponse: () -> Response<FoodRecipeResponseDto> = {
            Response.success(FoodRecipeResponseDto(emptyList()))
        }

        override suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipeResponseDto> {
            lastGetQueries = queries
            return getRecipesResponse()
        }

        override suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipeResponseDto> {
            lastSearchQueries = searchQuery
            return searchRecipesResponse()
        }

        override suspend fun getFoodJoke(apiKey: String): Response<FoodJokeDto> {
            throw UnsupportedOperationException()
        }
    }

    private class FakeLocalDataSource : RecipesLocalDataSource {
        val recipesFlow = MutableStateFlow<List<RecipesEntity>>(emptyList())
        val insertedRecipes = mutableListOf<RecipesEntity>()

        override fun readRecipes(): Flow<List<RecipesEntity>> = recipesFlow

        override suspend fun insertRecipes(recipesEntity: RecipesEntity) {
            insertedRecipes.add(recipesEntity)
            recipesFlow.value = listOf(recipesEntity)
        }

        override fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> = flowOf(emptyList())
        override suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) = Unit
        override suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) = Unit
        override suspend fun deleteFavoriteRecipes(favoritesEntities: List<FavoritesEntity>) = Unit
        override suspend fun deleteAllFavoriteRecipes() = Unit
        override fun readFoodJoke(): Flow<List<FoodJokeEntity>> = flowOf(emptyList())
        override suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) = Unit
    }

    @Test
    fun `fetchRecipes sends filter queries caches recipes and updates observed flow`() = runTest {
        val remote = FakeRemoteDataSource().apply {
            getRecipesResponse = {
                Response.success(
                    FoodRecipeResponseDto(
                        results = listOf(RecipeDto(id = 1, title = "Soup"))
                    )
                )
            }
        }
        val local = FakeLocalDataSource()
        val repository = RecipesRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val filter = MealAndDietFilter("soup", 1, "vegan", 2)
        val result = repository.fetchRecipes(filter)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf("Soup"), (result as AppResult.Success).data.map { it.title })
        assertEquals("soup", remote.lastGetQueries?.get(DataConstants.QUERY_TYPE))
        assertEquals("vegan", remote.lastGetQueries?.get(DataConstants.QUERY_DIET))
        assertEquals("true", remote.lastGetQueries?.get(DataConstants.QUERY_ADD_RECIPE_INFORMATION))
        assertEquals("true", remote.lastGetQueries?.get(DataConstants.QUERY_FILL_INGREDIENTS))
        assertEquals(1, local.insertedRecipes.size)
        assertEquals(listOf("Soup"), repository.observeCachedRecipes().first().map { it.title })
    }

    @Test
    fun `fetchRecipes returns NotFound and skips caching when API returns empty results`() = runTest {
        val remote = FakeRemoteDataSource().apply {
            getRecipesResponse = { Response.success(FoodRecipeResponseDto(emptyList())) }
        }
        val local = FakeLocalDataSource()
        val repository = RecipesRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val result = repository.fetchRecipes(MealAndDietFilter.Default)

        assertEquals(AppResult.Error(DataError.NotFound), result)
        assertTrue(local.insertedRecipes.isEmpty())
    }

    @Test
    fun `fetchRecipes returns NoInternet when offline`() = runTest {
        val remote = FakeRemoteDataSource()
        val local = FakeLocalDataSource()
        val repository = RecipesRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = false)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val result = repository.fetchRecipes(MealAndDietFilter.Default)

        assertEquals(AppResult.Error(DataError.NoInternet), result)
        assertEquals(null, remote.lastGetQueries)
    }

    @Test
    fun `searchRecipes returns matching recipes without modifying main cache`() = runTest {
        val remote = FakeRemoteDataSource().apply {
            searchRecipesResponse = {
                Response.success(
                    FoodRecipeResponseDto(
                        results = listOf(RecipeDto(id = 44, title = "Carrot Cake"))
                    )
                )
            }
        }
        val local = FakeLocalDataSource()
        val repository = RecipesRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val result = repository.searchRecipes("carrot")

        assertTrue(result is AppResult.Success)
        assertEquals(listOf("Carrot Cake"), (result as AppResult.Success).data.map { it.title })
        assertEquals("carrot", remote.lastSearchQueries?.get(DataConstants.QUERY_SEARCH))
        assertTrue(local.insertedRecipes.isEmpty())
    }
}
