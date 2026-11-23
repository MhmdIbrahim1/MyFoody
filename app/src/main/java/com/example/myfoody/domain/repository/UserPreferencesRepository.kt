package com.example.myfoody.domain.repository

import com.example.myfoody.domain.model.MealAndDietFilter
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeMealAndDietFilter(): Flow<MealAndDietFilter>
    suspend fun saveMealAndDietFilter(filter: MealAndDietFilter)
    fun observeBackOnline(): Flow<Boolean>
    suspend fun saveBackOnline(backOnline: Boolean)
}
