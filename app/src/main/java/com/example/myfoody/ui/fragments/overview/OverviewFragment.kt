package com.example.myfoody.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.example.myfoody.R
import com.example.myfoody.bindingAdapters.RecipeRowBinding
import com.example.myfoody.databinding.FragmentOverviewBinding
import com.example.myfoody.models.Result
import com.example.myfoody.util.Constants.Companion.RECIPE_RESULT_KEY
import com.example.myfoody.util.retrieveParcelable

/**
 * Fragment for displaying an overview of a recipe.
 */
class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(layoutInflater, container, false)

        // Retrieve recipe details from arguments bundle
        val args = arguments
        val myBundle: Result? = args!!.retrieveParcelable(RECIPE_RESULT_KEY) as Result?
        if (myBundle != null){
            // Populate the layout views with recipe data
            binding.mainImageView.load(myBundle.image)
            binding.timeTextView.text = myBundle.title
            binding.likesTextView.text = myBundle.aggregateLikes.toString()
            binding.timeTextView.text = myBundle.readyInMinutes.toString()
            // Use Jsoup library to remove HTML tags from the recipe summary

            RecipeRowBinding.parseHtml(binding.summaryTextView, myBundle.summary)

            // Adjust visibility and color of dietary flags based on recipe properties
            updateColors(myBundle.vegetarian, binding.vegetarianTextView, binding.vegetarianImageView)
            updateColors(myBundle.vegan, binding.veganTextView, binding.veganImageView)
            updateColors(myBundle.cheap, binding.cheapTextView, binding.cheapImageView)
            updateColors(myBundle.dairyFree, binding.dairyFreeTextView, binding.dairyFreeImageView)
            updateColors(myBundle.glutenFree, binding.glutenFreeTextView, binding.glutenFreeImageView)
            updateColors(myBundle.veryHealthy, binding.healthyTextView, binding.healthyImageView)
        }
        return binding.root
    }

    private fun updateColors(stateIsOn:Boolean, textView: TextView, imageView: ImageView) {
        if (stateIsOn) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}