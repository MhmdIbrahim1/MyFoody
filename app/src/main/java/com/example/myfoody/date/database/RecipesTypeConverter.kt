package com.example.myfoody.date.database

import androidx.room.TypeConverter
import com.example.myfoody.models.FoodRecipe
import com.example.myfoody.models.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Class representing a RecipesTypeConverter, which is a custom TypeConverter for the Room database.
 *
 * Handles converting complex data types (e.g., FoodRecipe) to and from JSON strings for storage in the database.
 */
class RecipesTypeConverter {

    // Create a Gson instance for handling the JSON conversion
   private var gson = Gson()

    /**
     * Converts a FoodRecipe object to a JSON string.
     *
     * @param foodRecipe The FoodRecipe object to convert.
     * @return A JSON string representation of the FoodRecipe object.
     */
    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String {
        return gson.toJson(foodRecipe)
    }

    /**
     * Converts a JSON string to a FoodRecipe object.
     *
     * @param data The JSON string to convert.
     * @return A FoodRecipe object represented by the JSON string.
     */
    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }


    /**
     * Converts a Result object to a JSON string.
     *
     * @param result The Result object to convert.
     * @return A JSON string representation of the Result object.
     */
    @TypeConverter
    fun resultToString(result: Result): String {
        return gson.toJson(result)
    }

    /**
     * Converts a JSON string to a Result object.
     *
     * @param data The JSON string to convert.
     * @return A Result object represented by the JSON string.
     */
    @TypeConverter
    fun stringToResult(data: String): Result {
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(data, listType)
    }
}