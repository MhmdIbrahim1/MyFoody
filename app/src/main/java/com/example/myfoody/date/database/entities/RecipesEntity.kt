package com.example.myfoody.date.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfoody.models.FoodRecipe
import com.example.myfoody.util.Constants.Companion.RECIPES_TABLE

/**
 * Class representing a RecipesEntity, which is an entity (table) in the Room database.
 *
 * Defines the structure of the table and its columns, including the primary key.
 */
@Entity(tableName = RECIPES_TABLE) // Declare the entity and set the table name using the RECIPES_TABLE constant
class RecipesEntity(
    var foodRecipe: FoodRecipe // Define a column in the table for storing FoodRecipe objects
) {
    @PrimaryKey(autoGenerate = false) // Declare the primary key for the table and set autoGenerate to false
    var id: Int = 0 // Define the ID column as the primary key

}