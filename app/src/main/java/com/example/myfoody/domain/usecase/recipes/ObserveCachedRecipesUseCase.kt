package com.example.myfoody.domain.usecase.recipes

import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.RecipesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCachedRecipesUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    operator fun invoke(): Flow<List<Recipe>> = repository.observeCachedRecipes()
}
