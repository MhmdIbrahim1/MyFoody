package com.example.myfoody.data.local.database

import androidx.room.TypeConverter
import com.example.myfoody.data.remote.dto.FoodRecipeResponseDto
import com.example.myfoody.data.remote.dto.RecipeDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipesTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipeResponseDto): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipeResponseDto {
        val listType = object : TypeToken<FoodRecipeResponseDto>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun recipeToString(recipe: RecipeDto): String {
        return gson.toJson(recipe)
    }

    @TypeConverter
    fun stringToRecipe(data: String): RecipeDto {
        val listType = object : TypeToken<RecipeDto>() {}.type
        return gson.fromJson(data, listType)
    }
}
