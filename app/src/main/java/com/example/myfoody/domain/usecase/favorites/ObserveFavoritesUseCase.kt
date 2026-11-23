package com.example.myfoody.domain.usecase.favorites

import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<FavoriteRecipe>> = repository.observeFavorites()
}
