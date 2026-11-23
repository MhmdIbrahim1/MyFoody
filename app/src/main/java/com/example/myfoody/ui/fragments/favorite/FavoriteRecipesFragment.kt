package com.example.myfoody.ui.fragments.favorite

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.myfoody.R
import com.example.myfoody.ui.theme.composeContent
import com.example.myfoody.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {
    private val mainViewModel by viewModels<MainViewModel>()
    private var selection by mutableStateOf(emptySet<Int>())
    private var actionMode: ActionMode? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        composeContent {
            val favorites by mainViewModel.readFavoriteRecipes.observeAsState(emptyList())
            FavoriteRecipesScreen(favorites, selection, onClick = { favorite ->
                if (actionMode != null) {
                    updateSelection(if (favorite.id in selection) selection - favorite.id else selection + favorite.id)
                } else {
                    findNavController().navigate(FavoriteRecipesFragmentDirections
                        .actionFavoriteRecipesFragmentToDetailsActivity(favorite.result))
                }
            }, onLongClick = { favorite ->
                if (actionMode == null) {
                    actionMode = requireActivity().startActionMode(selectionMenu)
                    if (actionMode != null) updateSelection(setOf(favorite.id))
                }
            })
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorite_recipes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId != R.id.deleteAll_favorite_recipes_menu) return false
                mainViewModel.deleteAllFavoriteRecipes()
                showMessage("All recipes removed.")
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { favorites ->
            if (selection.isNotEmpty()) updateSelection(selection.intersect(favorites.map { it.id }.toSet()))
        }
    }

    private fun updateSelection(ids: Set<Int>) {
        selection = ids
        if (ids.isEmpty()) {
            actionMode?.finish()
        } else {
            actionMode?.title = if (ids.size == 1) "1 item selected" else "${ids.size} items selected"
        }
    }

    private val selectionMenu = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.favorite_contextual_menu, menu)
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.contextualStatusBarColor)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            if (item.itemId != R.id.delete_favorite_recipe_menu) return false
            val selected = mainViewModel.readFavoriteRecipes.value.orEmpty().filter { it.id in selection }
            selected.forEach(mainViewModel::deleteFavoriteRecipe)
            showMessage("${selected.size} Recipe/s removed.")
            mode.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            selection = emptySet()
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        }
    }

    private fun showMessage(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).setAction("Okay") {}.show()
    }

    override fun onDestroyView() {
        actionMode?.finish()
        super.onDestroyView()
    }
}
