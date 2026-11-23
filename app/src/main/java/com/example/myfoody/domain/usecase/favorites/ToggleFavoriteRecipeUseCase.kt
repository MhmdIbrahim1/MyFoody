package com.example.myfoody.domain.usecase.favorites

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.FavoritesRepository
import javax.inject.Inject

class ToggleFavoriteRecipeUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(recipe: Recipe, existingFavorite: FavoriteRecipe?): Boolean {
        return if (existingFavorite == null) {
            repository.addFavorite(recipe)
            true
        } else {
            repository.deleteFavorite(existingFavorite)
            false
        }
    }
}
