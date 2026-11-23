package com.example.myfoody.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.data.remote.dto.FoodJokeDto

@Entity(tableName = DataConstants.FOOD_JOKE_TABLE)
data class FoodJokeEntity(
    @Embedded
    val foodJoke: FoodJokeDto,
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0
)
