package com.example.myfoody.presentation.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myfoody.R
import com.example.myfoody.presentation.navigation.toParcelable
import com.example.myfoody.presentation.theme.composeContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    private val args by navArgs<RecipesFragmentArgs>()
    private val viewModel by viewModels<RecipesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = composeContent {
        val uiState by viewModel.uiState.collectAsState()
        RecipesScreen(
            uiState = uiState,
            openRecipe = viewModel::onRecipeClicked,
            openFilters = viewModel::onFiltersClicked
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch(start = CoroutineStart.UNDISPATCHED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is RecipesUiEvent.ShowToast -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                            }
                            is RecipesUiEvent.NavigateToDetails -> {
                                findNavController().navigate(
                                    RecipesFragmentDirections
                                        .actionRecipesFragmentToDetailsActivity(event.recipe.toParcelable())
                                )
                            }
                            RecipesUiEvent.NavigateToFilters -> {
                                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                            }
                        }
                    }
                }
                viewModel.startObserving(backFromBottomSheet = args.backFromBottomSheet)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let(viewModel::onSearchSubmitted)
        return true
    }

    override fun onQueryTextChange(newText: String?) = true
}
