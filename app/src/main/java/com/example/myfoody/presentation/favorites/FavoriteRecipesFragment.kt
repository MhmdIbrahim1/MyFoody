package com.example.myfoody.presentation.favorites

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myfoody.R
import com.example.myfoody.presentation.navigation.toParcelable
import com.example.myfoody.presentation.theme.composeContent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {
    private val viewModel by viewModels<FavoritesViewModel>()
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = composeContent {
        val uiState by viewModel.uiState.collectAsState()
        FavoriteRecipesScreen(
            uiState = uiState,
            onClick = viewModel::onFavoriteClicked,
            onLongClick = viewModel::onFavoriteLongClicked
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorite_recipes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId != R.id.deleteAll_favorite_recipes_menu) return false
                viewModel.onDeleteAllClicked()
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        syncActionMode(state)
                    }
                }
                launch {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is FavoritesUiEvent.NavigateToDetails -> {
                                findNavController().navigate(
                                    FavoriteRecipesFragmentDirections
                                        .actionFavoriteRecipesFragmentToDetailsActivity(event.recipe.toParcelable())
                                )
                            }
                            is FavoritesUiEvent.ShowSnackbar -> {
                                Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT)
                                    .setAction("Okay") {}
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun syncActionMode(state: FavoritesUiState) {
        if (state.isSelectionMode) {
            if (actionMode == null) {
                actionMode = requireActivity().startActionMode(selectionMenu)
            }
            actionMode?.title = state.selectionTitle
        } else {
            actionMode?.finish()
        }
    }

    private val selectionMenu = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.favorite_contextual_menu, menu)
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.contextualStatusBarColor)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            if (item.itemId != R.id.delete_favorite_recipe_menu) return false
            viewModel.onDeleteSelectedClicked()
            mode.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.statusBarColor)
            viewModel.onClearSelection()
        }
    }

    override fun onDestroyView() {
        actionMode?.finish()
        super.onDestroyView()
    }
}
