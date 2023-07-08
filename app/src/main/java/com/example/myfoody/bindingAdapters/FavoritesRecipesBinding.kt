package com.example.myfoody.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfoody.adapters.FavoriteRecipesAdapter
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.date.database.entities.FoodJokeEntity
import com.example.myfoody.models.FoodJoke
import com.example.myfoody.util.NetworkResult

class FavoritesRecipesBinding {

    companion object {
        /**
         * This function is used to set the visibility of the views in the FavoriteRecipesFragment
         * depending on the data that is stored in the local database.
         * @param view - the view that is being passed in (ImageView, TextView, RecyclerView)
         * @param favoritesEntity - the data that is stored in the local database
         * @param mAdapter - the adapter that is used to set the data to the RecyclerView
         */
        @BindingAdapter("viewVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setDataAndViewVisibility(
            view: View,
            favoritesEntity: List<FavoritesEntity>?,
            mAdapter: FavoriteRecipesAdapter?
        ) {
            if (favoritesEntity.isNullOrEmpty()) {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.VISIBLE
                    }

                    is TextView -> {
                        view.visibility = View.VISIBLE
                    }

                    is RecyclerView -> {
                        view.visibility = View.INVISIBLE
                    }
                }
            } else {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.INVISIBLE
                    }

                    is TextView -> {
                        view.visibility = View.INVISIBLE
                    }

                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        mAdapter?.setData(favoritesEntity)
                    }
                }
            }
        }

        // another way to write the above function
//        @BindingAdapter("viewVisibility", "setData", requireAll = false)
//        @JvmStatic
//        fun setVisibility(
//            view: View,
//            favoritesEntity: List<FavoritesEntity>?,
//            mAdapter: FavoriteRecipesAdapter?
//        ) {
//           when (view){
//               is RecyclerView -> {
//                   val dataCheck = favoritesEntity.isNullOrEmpty()
//                   view.isInvisible = dataCheck
//                   if (!dataCheck){
//                       favoritesEntity?.let { mAdapter?.setData(it) }
//                   }
//               }
//               else -> {
//                   view.isVisible = favoritesEntity.isNullOrEmpty()
//               }
//           }
//        }
        /**
         * This function is used to set the visibility of the views in the FoodJokeFragment
         * depending on the data that is stored in the local database.
         * @param view - the view that is being passed in (ProgressBar, CardView)
         * @param apiResponse - the data that is being retrieved from the API
         * @param database - the data that is stored in the local database
         */
        @BindingAdapter("readApiResponse4", "readDatabase4", requireAll = false)
        @JvmStatic
        fun setErrorViewsVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ) {
            if (database != null) {
                if (database.isEmpty()) {
                    view.visibility = View.VISIBLE
                    if (view is TextView) {
                        if (apiResponse != null) {
                            view.text = apiResponse.message.toString()
                        }
                    }
                }
            }
            if (apiResponse is NetworkResult.Success) {
                view.visibility = View.INVISIBLE
            }

        }
    }
}