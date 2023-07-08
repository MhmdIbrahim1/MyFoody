package com.example.myfoody.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfoody.adapters.IngredientsAdapter
import com.example.myfoody.databinding.FragmentIngredientsBinding
import com.example.myfoody.models.Result
import com.example.myfoody.util.Constants.Companion.RECIPE_RESULT_KEY
import com.example.myfoody.util.retrieveParcelable

class Ingredients : Fragment() {

    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentIngredientsBinding.inflate(layoutInflater, container, false)

        // Retrieve recipe details from arguments bundle
        val args = arguments
        val myBundle: Result? = args?.retrieveParcelable(RECIPE_RESULT_KEY)

        // Set up the RecyclerView
        setupRecyclerView()

        // Populate the layout views with recipe data
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }

        return binding.root
    }

    // Set up the RecyclerView
    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.adapter = mAdapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}