package com.example.myfoody.data.repository

import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.data.mapper.toDomain
import com.example.myfoody.data.mapper.toEntity
import com.example.myfoody.data.mapper.toFavoriteEntity
import com.example.myfoody.di.IoDispatcher
import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe
import com.example.myfoody.domain.repository.FavoritesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val localDataSource: RecipesLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FavoriteRecipe>> {
        return localDataSource.readFavoriteRecipes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(recipe: Recipe) = withContext(ioDispatcher) {
        localDataSource.insertFavoriteRecipe(recipe.toFavoriteEntity())
    }

    override suspend fun deleteFavorite(favoriteRecipe: FavoriteRecipe) = withContext(ioDispatcher) {
        localDataSource.deleteFavoriteRecipe(favoriteRecipe.toEntity())
    }

    override suspend fun deleteFavorites(favoriteRecipes: List<FavoriteRecipe>) = withContext(ioDispatcher) {
        localDataSource.deleteFavoriteRecipes(favoriteRecipes.map { it.toEntity() })
    }

    override suspend fun deleteAllFavorites() = withContext(ioDispatcher) {
        localDataSource.deleteAllFavoriteRecipes()
    }
}
