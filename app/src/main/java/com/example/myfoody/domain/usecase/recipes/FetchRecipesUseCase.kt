package com.example.myfoody.domain.usecase.recipes

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.RecipesRepository
import com.example.myfoody.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FetchRecipesUseCase @Inject constructor(
    private val recipesRepository: RecipesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): AppResult<List<Recipe>> {
        val filter = userPreferencesRepository.observeMealAndDietFilter().first()
        return recipesRepository.fetchRecipes(filter)
    }
}
