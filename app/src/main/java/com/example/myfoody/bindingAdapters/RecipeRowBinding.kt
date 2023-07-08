package com.example.myfoody.bindingAdapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.myfoody.R
import com.example.myfoody.models.Result
import com.example.myfoody.ui.fragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup

/**
 * RecipeRowBinding class contains custom binding adapters for recipe row layout.
 */
class RecipeRowBinding {

    companion object {

        /**
         * Sets an on click listener for the recipe row layout.
         *
         * @param recipeRowLayout The recipe row layout.
         * @param result The recipe object.
         */
        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: Result) {
            Log.d("onRecipeClickListener", "called!!")
            try {
                val action =
                    RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                recipeRowLayout.setOnClickListener {
                    recipeRowLayout.findNavController().navigate(action)
                }
            } catch (e: Exception) {
                Log.d("onRecipeClickListener", "onRecipeClickListener: ${e.message}")
            }
        }


        /**
         * Applies the "vegan" color to a View (TextView or ImageView) if the given boolean is true.
         *
         * @param view The View (TextView or ImageView) to apply the color to.
         * @param vegan A boolean representing whether the recipe is vegan or not.
         */
        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }

                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }


        /**
         * Loads an image from a URL into an ImageView.
         *
         * @param imageView The ImageView to load the image into.
         * @param imageUrl The URL of the image to load.
         */
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }

        /**
         * Parses HTML text into plain text and sets it as the text for a TextView.
         *
         * @param textView The TextView to set the text on.
         * @param description The HTML text to be parsed.
         */
        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?) {
            if (description != null) {
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }
    }
}