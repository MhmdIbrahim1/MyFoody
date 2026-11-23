package com.example.myfoody.domain.usecase.favorites

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.repository.FavoritesRepository
import javax.inject.Inject

class DeleteFavoriteRecipesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(favorites: List<FavoriteRecipe>) {
        if (favorites.isEmpty()) return
        repository.deleteFavorites(favorites)
    }
}
