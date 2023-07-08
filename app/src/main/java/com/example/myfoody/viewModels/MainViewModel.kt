package com.example.myfoody.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfoody.date.Repository
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.date.database.entities.FoodJokeEntity
import com.example.myfoody.date.database.entities.RecipesEntity
import com.example.myfoody.models.FoodJoke
import com.example.myfoody.models.FoodRecipe
import com.example.myfoody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

/**
 * MainViewModel class that acts as a communication layer between the UI and the data sources.
 *
 * @property repository An instance of Repository for accessing data sources.
 * @property Application An instance of Application, used for accessing the system services.
 */


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /* Room Database */

    var recyclerViewState: Parcelable? = null
    // LiveData that holds the list of recipes from the local database.
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()

    // LiveData that holds the list of favorite recipes from the local database.
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> =
        repository.local.readFavoriteRecipes().asLiveData()

    // LiveData that holds the list of food jokes from the local database.
    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    /**
     * Function to insert recipes into the local database.
     *
     * @param recipesEntity The RecipesEntity object to be inserted.
     */
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    /**
     * Function to insert favorite recipes into the local database.
     * @param favoritesEntity The FavoritesEntity object to be inserted.
     */
     fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipe(favoritesEntity)
        }

    /**
     * Function to insert a food joke into the local database.
     * @param foodJokeEntity The FoodJokeEntity object to be inserted.
     */
    private fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFoodJoke(foodJokeEntity)
        }

    /**
     * Function to delete a favorite recipe from the local database.
     * @param favoritesEntity The FavoritesEntity object to be deleted.
     */
     fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    /**
     * Function to delete all favorite recipes from the local database.
     */
     fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    //----------------------------------------------------------------------------------------------

    /* Retrofit */

    // MutableLiveData that holds the network result of the FoodRecipe request.
    private val _recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    val recipesResponse: LiveData<NetworkResult<FoodRecipe>> get() = _recipesResponse

    // MutableLiveData that holds the network result of the searched recipes request.
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    // MutableLiveData that holds the network result of the food joke request.
    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()


    // Function to get recipes using the provided query parameters.
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    // Function to search recipes using the provided query parameters.
    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }
    
    // Function to get a random food joke.
    fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }


    /**
     * Function to get recipes using the provided query parameters.
     *
     * @param queries The query parameters to be used in the request.
     */

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        // Set the value of _recipesResponse to Loading.
        _recipesResponse.value = NetworkResult.Loading()
        // Check if the device has an active internet connection.
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                _recipesResponse.value = handleFoodRecipesResponse(response)

                // Cache the recipes in the local database.
                val foodRecipe = _recipesResponse.value!!.data
                // Check if the response is not null. If it is not null, cache the recipes.
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e: Exception) {
                _recipesResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        } else {
            _recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Function to handle the response from the FoodRecipesApi.
     *
     * @param response The response from the FoodRecipesApi.
     * @return A NetworkResult object containing a FoodRecipe object if the request is successful.
     */
    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Function to handle the response from the FoodJokeApi.
     *
     * @param response The response from the FoodJokeApi.
     * @return A NetworkResult object containing a FoodJoke object if the request is successful.
     */
    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFoodJoke(apiKey)

                val foodJoke = foodJokeResponse.value!!.data

                // Check if the response is not null. If it is not null, cache the food joke.
                if (foodJoke != null) {
                    offlineCacheFoodJoke(foodJoke)
                }
                foodJokeResponse.value = handleFoodJokeResponse(response)
            } catch (e: Exception) {
                foodJokeResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        } else {
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }


    /**
     * Function to cache the recipes in the local database.
     *
     * @param foodRecipe The FoodRecipe object to be cached.
     */
    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    /**
     * Function to cache the food joke in the local database.
     *
     * @param foodJoke The FoodJoke object to be cached.
     */
    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }


    /**
     * Function to handle the response from the FoodRecipe request.
     *
     * @param response The response from the FoodRecipe request.
     * @return A NetworkResult object that holds the result of the request.
     */
    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    /**
     * Function to handle the response from the FoodJoke request.
     *
     * @param response The response from the FoodJoke request.
     * @return A NetworkResult object that holds the result of the request.
     */
    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }
    /**
     * Function to check if the device has an active internet connection.
     *
     * @return A boolean value that indicates if the device has an active internet connection.
     */
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> return false
        }
    }
}