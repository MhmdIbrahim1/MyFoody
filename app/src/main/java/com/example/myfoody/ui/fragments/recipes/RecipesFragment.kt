package com.example.myfoody.ui.fragments.recipes

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myfoody.R
import com.example.myfoody.ui.theme.composeContent
import com.example.myfoody.util.NetworkListener
import com.example.myfoody.util.NetworkResult
import com.example.myfoody.viewModels.MainViewModel
import com.example.myfoody.viewModels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class)
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    private val args by navArgs<RecipesFragmentArgs>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val recipesViewModel by activityViewModels<RecipesViewModel>()
    private var query by mutableStateOf<String?>(null)
    private var loadingFromNetwork by mutableStateOf(false)
    private var lastConnectivity: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = savedInstanceState?.getString("searchQuery")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchQuery", query)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        composeContent {
            val database by mainViewModel.readRecipes.observeAsState()
            val searched by mainViewModel.searchedRecipesResponse.observeAsState()
            val fetched by mainViewModel.recipesResponse.observeAsState()
            val cached = database?.firstOrNull()?.foodRecipe
            val response = when {
                query != null -> searched
                loadingFromNetwork -> fetched
                else -> null
            }
            val isLoading = response is NetworkResult.Loading ||
                (response == null && (cached == null || query != null || loadingFromNetwork))
            val error = (response as? NetworkResult.Error)?.message
            LaunchedEffect(response) {
                if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
            RecipesScreen(
                recipes = response?.data?.results ?: cached?.results.orEmpty(),
                loading = isLoading,
                error = error.takeIf { cached == null },
                openRecipe = { recipe ->
                    findNavController().navigate(RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(recipe))
                },
                openFilters = {
                    if (recipesViewModel.networkStatus) {
                        findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                    } else {
                        recipesViewModel.showNetworkStatus()
                    }
                }
            )
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)
                (menu.findItem(R.id.menu_search).actionView as? SearchView)?.apply {
                    isSubmitButtonEnabled = true
                    setOnQueryTextListener(this@RecipesFragment)
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) { recipesViewModel.backOnline = it }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val listener = NetworkListener()
                val manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                try {
                    listener.checkNetworkAvailability(requireContext()).collect { connected ->
                        recipesViewModel.networkStatus = connected
                        if (lastConnectivity != connected) {
                            recipesViewModel.showNetworkStatus()
                            loadForConnectivity()
                            lastConnectivity = connected
                        }
                    }
                } finally {
                    manager.unregisterNetworkCallback(listener)
                }
            }
        }
    }

    private suspend fun loadForConnectivity() {
        val activeQuery = query
        if (activeQuery != null) {
            if (mainViewModel.searchedRecipesResponse.value == null ||
                mainViewModel.searchedRecipesResponse.value is NetworkResult.Error) {
                submitSearch(activeQuery)
            }
            return
        }
        val cached = mainViewModel.readRecipes.asFlow().first()
        if (query != null) return
        loadingFromNetwork = cached.isEmpty() || args.backFromBottomSheet
        if (loadingFromNetwork) {
            mainViewModel.getRecipes(recipesViewModel.applyQueries())
        }
    }

    private fun submitSearch(text: String) {
        query = text
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(text))
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let(::submitSearch)
        return true
    }

    override fun onQueryTextChange(newText: String?) = true
}
