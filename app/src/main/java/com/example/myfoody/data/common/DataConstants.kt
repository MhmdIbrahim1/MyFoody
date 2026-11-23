package com.example.myfoody.data.common

object DataConstants {
    const val BASE_URL = "https://api.spoonacular.com"
    const val BASE_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/"
    const val API_KEY = "33a21325aa944e6eb74aa2968a3e66e4"

    // API Query Keys
    const val QUERY_SEARCH = "query"
    const val QUERY_NUMBER = "number"
    const val QUERY_API_KEY = "apiKey"
    const val QUERY_TYPE = "type"
    const val QUERY_DIET = "diet"
    const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
    const val QUERY_FILL_INGREDIENTS = "fillIngredients"
    const val DEFAULT_RECIPES_NUMBER = "70"

    // Room Database
    const val DATABASE_NAME = "recipes_database"
    const val RECIPES_TABLE = "recipes_table"
    const val FAVORITE_RECIPES_TABLE = "favorite_recipes_table"
    const val FOOD_JOKE_TABLE = "food_joke_table"

    // DataStore Preferences
    const val PREFERENCE_NAME = "foody_preferences"
    const val PREFERENCE_MEAL_TYPE = "mealType"
    const val PREFERENCE_MEAL_TYPE_ID = "mealTypeId"
    const val PREFERENCE_DIET_TYPE = "dietType"
    const val PREFERENCE_DIET_TYPE_ID = "dietTypeId"
    const val PREFERENCE_BACK_ONLINE = "backOnline"
}
