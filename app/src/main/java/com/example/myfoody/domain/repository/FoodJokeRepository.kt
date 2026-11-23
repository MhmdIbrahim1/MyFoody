package com.example.myfoody.domain.repository

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.model.FoodJoke
import kotlinx.coroutines.flow.Flow

interface FoodJokeRepository {
    fun observeCachedFoodJoke(): Flow<FoodJoke?>
    suspend fun fetchFoodJoke(): AppResult<FoodJoke>
}
