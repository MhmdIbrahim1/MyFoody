package com.example.myfoody.data.local.datasource

import com.example.myfoody.data.local.database.RecipesDao
import com.example.myfoody.data.local.database.entity.FavoritesEntity
import com.example.myfoody.data.local.database.entity.FoodJokeEntity
import com.example.myfoody.data.local.database.entity.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipesLocalDataSourceImpl @Inject constructor(
    private val recipesDao: RecipesDao
) : RecipesLocalDataSource {

    override fun readRecipes(): Flow<List<RecipesEntity>> = recipesDao.readRecipes()

    override suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    override fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> = recipesDao.readFavoriteRecipes()

    override suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    override suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    override suspend fun deleteFavoriteRecipes(favoritesEntities: List<FavoritesEntity>) {
        recipesDao.deleteFavoriteRecipes(favoritesEntities)
    }

    override suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

    override fun readFoodJoke(): Flow<List<FoodJokeEntity>> = recipesDao.readFoodJoke()

    override suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipesDao.insertFoodJoke(foodJokeEntity)
    }
}
