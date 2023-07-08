package com.example.myfoody.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfoody.date.DataStoreRepository
import com.example.myfoody.date.MealAndDietType
import com.example.myfoody.util.Constants.Companion.API_KEY
import com.example.myfoody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.myfoody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.myfoody.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.example.myfoody.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.myfoody.util.Constants.Companion.QUERY_API_KEY
import com.example.myfoody.util.Constants.Companion.QUERY_DIET
import com.example.myfoody.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.myfoody.util.Constants.Companion.QUERY_NUMBER
import com.example.myfoody.util.Constants.Companion.QUERY_SEARCH
import com.example.myfoody.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataSoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE
    private lateinit var mealAndDiet: MealAndDietType

    var networkStatus = false
    var backOnline = false

    // Read the meal and diet type preferences using DataStoreRepository.
    val readMealAndDietType = dataSoreRepository.readMealAndDietType
    val readBackOnline = dataSoreRepository.readBackOnline.asLiveData()

    /**
     * Get recipes from the API call.
     */
    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            if (this@RecipesViewModel::mealAndDiet.isInitialized) {
                dataSoreRepository.saveMealAndDietType(
                    mealAndDiet.selectedMealType,
                    mealAndDiet.selectedMealTypeId,
                    mealAndDiet.selectedDietType,
                    mealAndDiet.selectedDietTypeId
                )
            }
        }

    /**
     * Save the meal and diet type preferences.
     *
     * @param mealType The selected meal type.
     * @param mealTypeId The ID of the selected meal type.
     * @param dietType The selected diet type.
     * @param dietTypeId The ID of the selected diet type.
     */
    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataSoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }


    /* Save the back online status. */
    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataSoreRepository.saveBackOnline(backOnline)
        }

    /**
     * Create a HashMap containing the query parameters required for the API call to search for recipes.
     * Reads the meal and diet type preferences using the DataStoreRepository and includes them in the query parameters.
     *
     * @return A HashMap with query parameters for the API call.
     */
    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    /**
     * Create a HashMap containing the query parameters required for the API call to search for recipes.
     * Reads the meal and diet type preferences using the DataStoreRepository and includes them in the query parameters.
     *
     * @param searchQuery The search query.
     * @return A HashMap with query parameters for the API call.
     */
    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    /**
     * Check the network status.
     * `true` if the device is connected to the internet, `false` otherwise.
     * If the device is connected to the internet, check if the back online status is `true`.
     * If the back online status is `true`, display a toast message.
     * If the device is not connected to the internet, display a toast message.
     * If the back online status is `true`, save the back online status as `false`.
     */
    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        }else if(networkStatus){
            if(backOnline){
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}