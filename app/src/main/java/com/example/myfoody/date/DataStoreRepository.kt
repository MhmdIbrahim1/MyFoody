package com.example.myfoody.date

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myfoody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.myfoody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.myfoody.util.Constants.Companion.PREFERENCE_BACK_ONLINE
import com.example.myfoody.util.Constants.Companion.PREFERENCE_DIET_TYPE
import com.example.myfoody.util.Constants.Companion.PREFERENCE_DIET_TYPE_ID
import com.example.myfoody.util.Constants.Companion.PREFERENCE_MEAL_TYPE
import com.example.myfoody.util.Constants.Companion.PREFERENCE_MEAL_TYPE_ID
import com.example.myfoody.util.Constants.Companion.PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = PREFERENCE_NAME)
@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    // Define the preference keys for meal type and diet type preferences.
    private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCE_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCE_MEAL_TYPE_ID)
        val selectedDietType =stringPreferencesKey(PREFERENCE_DIET_TYPE)
        val selectedDietTypeId =intPreferencesKey(PREFERENCE_DIET_TYPE_ID)
        val selectedBackOnline = booleanPreferencesKey(PREFERENCE_BACK_ONLINE)
    }

    // Create a DataStore instance to store preferences.
    private val dataStore: DataStore<Preferences> = context.dataStore

    /**
     * Save the meal and diet type preferences.
     *
     * @param mealType The selected meal type.
     * @param mealTypeId The ID of the selected meal type.
     * @param dietType The selected diet type.
     * @param dietTypeId The ID of the selected diet type.
     */
    suspend fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMealType] = mealType
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId
        }
    }

    /**
     * Save the back online status to the data store.
     *
     * @param backOnline Boolean to save to the data store.
     */
    suspend fun saveBackOnline(backOnline: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedBackOnline] = backOnline
        }
    }
    /**
     * A Flow that emits the current meal and diet type preferences as a MealAndDietType object.
     */
    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
        .catch {
            exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map {
            preferences ->
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }


    /* A Flow that emits the current back online status as a boolean value. */
    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch {
                exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map {
                preferences ->
            val backOnline = preferences[PreferenceKeys.selectedBackOnline] ?: false
            backOnline
        }
}

/**
 * A data class that represents the meal and diet type preferences.
 *
 * @param selectedMealType The selected meal type.
 * @param selectedMealTypeId The ID of the selected meal type.
 * @param selectedDietType The selected diet type.
 * @param selectedDietTypeId The ID of the selected diet type.
 */
data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)