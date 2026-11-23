package com.example.myfoody.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.remote.dto.RecipeDto

@Entity(tableName = DataConstants.FAVORITE_RECIPES_TABLE)
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val result: RecipeDto
)
