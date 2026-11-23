package com.example.myfoody.domain.usecase.recipes

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.RecipesRepository
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<Recipe>> {
        return repository.searchRecipes(query)
    }
}
