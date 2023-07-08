package com.example.myfoody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfoody.viewModels.MainViewModel
import com.example.myfoody.R
import com.example.myfoody.adapters.RecipesAdapter
import com.example.myfoody.databinding.FragmentRecipesBinding
import com.example.myfoody.util.NetworkListener
import com.example.myfoody.util.NetworkResult
import com.example.myfoody.util.observeOnce
import com.example.myfoody.viewModels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * RecipesFragment class displays a list of recipes to the user using a RecyclerView.
 */

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    // Holds the reference to the RecipesFragmentArgs

    // Holds the reference to the RecipesFragmentArgs
    private val args by navArgs<RecipesFragmentArgs>()

    // ViewBinding variable for the fragment (private because it should not be accessed from outside the class)
    private var _binding: FragmentRecipesBinding? = null

    // ViewBinding variable for the fragment (public because it will be accessed from the adapter)
    private val binding get() = _binding!!

    // Lazily initializes the RecipesAdapter
    private val mAdapter by lazy { RecipesAdapter() }

    // Holds the reference to the MainViewModel
    private lateinit var mainViewModel: MainViewModel

    // Holds the reference to the RecipesViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    // Holds the reference to the NetworkListener
    private lateinit var networkListener: NetworkListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the MainViewModel
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // Initialize the RecipesViewModel
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize the ViewBinding variable for the fragment and inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        // Set the MainViewModel for the binding variable to the MainViewModel
        binding.mainViewModel = mainViewModel

        // enable the options menu in the fragment
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                // Initialize the search menu item and the search view
                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu item selection

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Set up the RecyclerView
        setUpRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }



        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(requireContext())
                    .collect { status ->
                        Log.d("NetworkListener", status.toString())
                        recipesViewModel.networkStatus = status
                        recipesViewModel.showNetworkStatus()
                        readDatabase()
                    }
            }
        }

        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }


        return binding.root
    }

    /**
     * Sets up the RecyclerView with an adapter, layout manager, and shimmer effect.
     */
    private fun setUpRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            // Search the API for the desired query
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    /**
     * Reads the recipes from the local database using the MainViewModel.
     */
    private fun readDatabase() {
        lifecycleScope.launch {
            // Observe the recipes from the local database
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                // If the database is not empty, set the data in the adapter
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    // If the database is empty, request data from the API
                    Log.d("RecipesFragment", "requestApiData called!")
                    requestApiData()
                }
            }
        }
    }

    /**
     * Requests data from the API using the MainViewModel.
     */
    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData called!")
        // Get the meal type and diet type from the RecipesViewModel
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        // Observe the response from the API
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                // If the response is successful, hide the shimmer effect and set the data in the adapter
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                    recipesViewModel.saveMealAndDietType()
                }
                // If the response is an error, hide the shimmer effect and display a toast message
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // If the response is loading, show the shimmer effect
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { mAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    /**
     * Loads the data from the local database using the MainViewModel.
     */
    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].foodRecipe)
                }
            }
        }
    }

    /**
     * Shows the shimmer effect in the RecyclerView.
     */
    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.recyclerview.visibility = View.GONE
    }

    /**
     * Hides the shimmer effect in the RecyclerView.
     */
    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}