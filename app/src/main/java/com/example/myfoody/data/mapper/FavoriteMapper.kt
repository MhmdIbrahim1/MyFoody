package com.example.myfoody.data.mapper

import com.example.myfoody.data.local.database.entity.FavoritesEntity
import com.example.myfoody.domain.model.FavoriteRecipe
import com.example.myfoody.domain.model.Recipe

fun FavoritesEntity.toDomain(): FavoriteRecipe = FavoriteRecipe(
    id = id,
    recipe = result.toDomain()
)

fun FavoriteRecipe.toEntity(): FavoritesEntity = FavoritesEntity(
    id = id,
    result = recipe.toDto()
)

fun Recipe.toFavoriteEntity(id: Int = 0): FavoritesEntity = FavoritesEntity(
    id = id,
    result = toDto()
)
