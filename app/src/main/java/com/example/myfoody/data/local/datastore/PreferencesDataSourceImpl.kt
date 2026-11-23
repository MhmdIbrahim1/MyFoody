package com.example.myfoody.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myfoody.data.common.DataConstants
import com.example.myfoody.domain.model.MealAndDietFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PreferencesDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesDataSource {

    private object Keys {
        val selectedMealType = stringPreferencesKey(DataConstants.PREFERENCE_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(DataConstants.PREFERENCE_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(DataConstants.PREFERENCE_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(DataConstants.PREFERENCE_DIET_TYPE_ID)
        val backOnline = booleanPreferencesKey(DataConstants.PREFERENCE_BACK_ONLINE)
    }

    override val mealAndDietFilter: Flow<MealAndDietFilter> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            MealAndDietFilter(
                selectedMealType = preferences[Keys.selectedMealType]
                    ?: MealAndDietFilter.DEFAULT_MEAL_TYPE,
                selectedMealTypeId = preferences[Keys.selectedMealTypeId] ?: 0,
                selectedDietType = preferences[Keys.selectedDietType]
                    ?: MealAndDietFilter.DEFAULT_DIET_TYPE,
                selectedDietTypeId = preferences[Keys.selectedDietTypeId] ?: 0
            )
        }

    override val backOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[Keys.backOnline] ?: false
        }

    override suspend fun saveMealAndDietFilter(filter: MealAndDietFilter) {
        dataStore.edit { preferences ->
            preferences[Keys.selectedMealType] = filter.selectedMealType
            preferences[Keys.selectedMealTypeId] = filter.selectedMealTypeId
            preferences[Keys.selectedDietType] = filter.selectedDietType
            preferences[Keys.selectedDietTypeId] = filter.selectedDietTypeId
        }
    }

    override suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.backOnline] = backOnline
        }
    }
}
