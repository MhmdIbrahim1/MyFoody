package com.example.myfoody.domain.usecase.foodjoke

import com.example.myfoody.domain.model.FoodJoke
import com.example.myfoody.domain.repository.FoodJokeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCachedFoodJokeUseCase @Inject constructor(
    private val repository: FoodJokeRepository
) {
    operator fun invoke(): Flow<FoodJoke?> = repository.observeCachedFoodJoke()
}
