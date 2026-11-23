package com.example.myfoody.data.local.datastore

import com.example.myfoody.domain.model.MealAndDietFilter
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val mealAndDietFilter: Flow<MealAndDietFilter>
    val backOnline: Flow<Boolean>
    suspend fun saveMealAndDietFilter(filter: MealAndDietFilter)
    suspend fun saveBackOnline(backOnline: Boolean)
}
