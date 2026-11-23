package com.example.myfoody.data.repository

import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.data.mapper.toDomainList
import com.example.myfoody.data.mapper.toRecipesEntity
import com.example.myfoody.data.remote.datasource.RecipesRemoteDataSource
import com.example.myfoody.data.remote.util.SafeApiCall
import com.example.myfoody.di.IoDispatcher
import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.RecipesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipesRepositoryImpl @Inject constructor(
    private val remoteDataSource: RecipesRemoteDataSource,
    private val localDataSource: RecipesLocalDataSource,
    private val safeApiCall: SafeApiCall,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RecipesRepository {

    override fun observeCachedRecipes(): Flow<List<Recipe>> {
        return localDataSource.readRecipes().map { entities ->
            entities.firstOrNull()?.toDomainList().orEmpty()
        }
    }

    override suspend fun fetchRecipes(filter: MealAndDietFilter): AppResult<List<Recipe>> =
        withContext(ioDispatcher) {
            val queries = mapOf(
                DataConstants.QUERY_NUMBER to DataConstants.DEFAULT_RECIPES_NUMBER,
                DataConstants.QUERY_API_KEY to DataConstants.API_KEY,
                DataConstants.QUERY_TYPE to filter.selectedMealType,
                DataConstants.QUERY_DIET to filter.selectedDietType,
                DataConstants.QUERY_ADD_RECIPE_INFORMATION to "true",
                DataConstants.QUERY_FILL_INGREDIENTS to "true"
            )
            when (val result = safeApiCall.execute { remoteDataSource.getRecipes(queries) }) {
                is AppResult.Success -> {
                    val recipes = result.data.toDomainList()
                    if (recipes.isEmpty()) {
                        AppResult.Error(DataError.NotFound)
                    } else {
                        localDataSource.insertRecipes(recipes.toRecipesEntity())
                        AppResult.Success(recipes)
                    }
                }
                is AppResult.Error -> result
            }
        }

    override suspend fun searchRecipes(query: String): AppResult<List<Recipe>> =
        withContext(ioDispatcher) {
            val queries = mapOf(
                DataConstants.QUERY_SEARCH to query,
                DataConstants.QUERY_NUMBER to DataConstants.DEFAULT_RECIPES_NUMBER,
                DataConstants.QUERY_API_KEY to DataConstants.API_KEY,
                DataConstants.QUERY_ADD_RECIPE_INFORMATION to "true",
                DataConstants.QUERY_FILL_INGREDIENTS to "true"
            )
            when (val result = safeApiCall.execute { remoteDataSource.searchRecipes(queries) }) {
                is AppResult.Success -> {
                    val recipes = result.data.toDomainList()
                    if (recipes.isEmpty()) {
                        AppResult.Error(DataError.NotFound)
                    } else {
                        AppResult.Success(recipes)
                    }
                }
                is AppResult.Error -> result
            }
        }
}
