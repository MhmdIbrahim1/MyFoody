package com.example.myfoody.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.example.myfoody.date.database.entities.RecipesEntity
import com.example.myfoody.models.FoodRecipe
import com.example.myfoody.util.NetworkResult

class RecipesBinding {

    companion object {

        /* Updated Code */
        @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ) {
            when (view){
                is ImageView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                }
                is TextView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }

//        /**
//         * Binding adapter for setting the error ImageView visibility based on the API response
//         * and the contents of the database.
//         *
//         * @param imageView The ImageView to set the visibility for.
//         * @param apiResponse The API response from the network call.
//         * @param database The list of RecipesEntity objects from the database.
//         */
//        @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)
//        @JvmStatic
//        fun errorImageViewVisibility(
//            imageView: ImageView,
//            apiResponse: NetworkResult<FoodRecipe>?,
//            database: List<RecipesEntity>?
//        ) {
//            when (apiResponse) {
//                is NetworkResult.Error -> {
//                    if (database.isNullOrEmpty()) {
//                        imageView.visibility = View.VISIBLE
//                    }
//                }
//                is NetworkResult.Loading -> imageView.visibility = View.INVISIBLE
//                is NetworkResult.Success -> imageView.visibility = View.INVISIBLE
//            }
//        }
//
//        /**
//         * Binding adapter for setting the error TextView visibility and text based on the API
//         * response and the contents of the database.
//         *
//         * @param textView The TextView to set the visibility and text for.
//         * @param apiResponse The API response from the network call.
//         * @param database The list of RecipesEntity objects from the database.
//         */
//        @BindingAdapter("readApiResponse2", "readDatabase2", requireAll = true)
//        @JvmStatic
//        fun errorTextViewVisibility(
//            textView: TextView,
//            apiResponse: NetworkResult<FoodRecipe>?,
//            database: List<RecipesEntity>?
//        ) {
//            when (apiResponse) {
//                is NetworkResult.Error -> {
//                    if (database.isNullOrEmpty()) {
//                        textView.visibility = View.VISIBLE
//                        textView.text = apiResponse.message.toString()
//                    }
//                }
//                is NetworkResult.Loading -> textView.visibility = View.INVISIBLE
//                is NetworkResult.Success -> textView.visibility = View.INVISIBLE
//            }
//        }
    }
}