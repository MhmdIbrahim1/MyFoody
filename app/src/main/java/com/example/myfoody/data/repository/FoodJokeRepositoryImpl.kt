package com.example.myfoody.data.repository

import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.data.mapper.toDomain
import com.example.myfoody.data.mapper.toEntity
import com.example.myfoody.data.remote.datasource.RecipesRemoteDataSource
import com.example.myfoody.data.remote.util.SafeApiCall
import com.example.myfoody.di.IoDispatcher
import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.map
import com.example.myfoody.domain.model.FoodJoke
import com.example.myfoody.domain.repository.FoodJokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FoodJokeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RecipesRemoteDataSource,
    private val localDataSource: RecipesLocalDataSource,
    private val safeApiCall: SafeApiCall,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FoodJokeRepository {

    override fun observeCachedFoodJoke(): Flow<FoodJoke?> {
        return localDataSource.readFoodJoke().map { entities ->
            entities.firstOrNull()?.toDomain()
        }
    }

    override suspend fun fetchFoodJoke(): AppResult<FoodJoke> = withContext(ioDispatcher) {
        val result = safeApiCall.execute {
            remoteDataSource.getFoodJoke(DataConstants.API_KEY)
        }.map { it.toDomain() }

        if (result is AppResult.Success) {
            localDataSource.insertFoodJoke(result.data.toEntity())
        }

        result
    }
}
