package com.example.myfoody.domain.usecase.foodjoke

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.model.FoodJoke
import com.example.myfoody.domain.repository.FoodJokeRepository
import javax.inject.Inject

class FetchFoodJokeUseCase @Inject constructor(
    private val repository: FoodJokeRepository
) {
    suspend operator fun invoke(): AppResult<FoodJoke> = repository.fetchFoodJoke()
}
