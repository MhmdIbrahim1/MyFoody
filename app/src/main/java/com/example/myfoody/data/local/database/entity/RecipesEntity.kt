package com.example.myfoody.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto

@Entity(tableName = DataConstants.RECIPES_TABLE)
data class RecipesEntity(
    val foodRecipe: FoodRecipeResponseDto,
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0
)
