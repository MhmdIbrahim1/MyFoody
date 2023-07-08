package com.example.myfoody.date.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfoody.models.Result
import com.example.myfoody.util.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)