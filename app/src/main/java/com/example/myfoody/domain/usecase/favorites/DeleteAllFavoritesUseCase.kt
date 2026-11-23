package com.example.myfoody.domain.usecase.favorites

import com.example.myfoody.domain.repository.FavoritesRepository
import javax.inject.Inject

class DeleteAllFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllFavorites()
    }
}
