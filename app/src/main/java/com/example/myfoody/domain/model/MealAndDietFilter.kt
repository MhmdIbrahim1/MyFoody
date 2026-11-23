package com.example.myfoody.domain.model

data class MealAndDietFilter(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
) {
    companion object {
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"

        val Default = MealAndDietFilter(
            selectedMealType = DEFAULT_MEAL_TYPE,
            selectedMealTypeId = 0,
            selectedDietType = DEFAULT_DIET_TYPE,
            selectedDietTypeId = 0
        )
    }
}
